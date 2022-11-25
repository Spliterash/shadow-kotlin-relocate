package ru.spliterash.shadowkotlinrelocate.shadowjar

import com.github.jengelman.gradle.plugins.shadow.relocation.Relocator
import com.github.jengelman.gradle.plugins.shadow.relocation.SimpleRelocator
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Action
import org.objectweb.asm.ClassReader
import ru.spliterash.shadowkotlinrelocate.shadowjar.KotlinRelocator.Companion.storeRelocationPath
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path

private class KotlinRelocator(private val delegate: SimpleRelocator) :
    Relocator by delegate {


    companion object {
        private val relocationPaths: MutableMap<ShadowJar, MutableMap<String, String>> = hashMapOf()
        private fun getRelocationPaths(shadowJar: ShadowJar) = relocationPaths.getOrPut(shadowJar) { hashMapOf() }

        internal fun ShadowJar.storeRelocationPath(pattern: String, destination: String) {
            val newPattern = pattern.replace('.', '/') + "/"
            val taskRelocationPaths = getRelocationPaths(this)
            val intersections = taskRelocationPaths.keys.filter { it.startsWith(newPattern) }
            require(intersections.isEmpty()) {
                "Can't relocate from $pattern to $destination as it clashes with another paths: ${intersections.joinToString()}"
            }
            taskRelocationPaths[newPattern] = destination.replace('.', '/') + "/"
        }

        private fun ShadowJar.patchFile(file: Path) {
            if (Files.isDirectory(file) || !file.toString().endsWith(".class")) return
            val taskRelocationPaths = getRelocationPaths(this)
            Files.newInputStream(file).use { ins ->
                val cr = ClassReader(ins)
                val cw = PatchedClassWriter(cr, 0, taskRelocationPaths)
                val scanner = AnnotationScanner(cw, taskRelocationPaths)
                cr.accept(scanner, 0)
                if (scanner.wasPatched || cw.wasPatched) {
                    ins.close()
                    Files.delete(file)
                    Files.write(file, cw.toByteArray())
                }
            }
        }

        fun patchMetadata(task: ShadowJar) {
            val zip = task.archiveFile.get().asFile.toPath()
            FileSystems.newFileSystem(zip, null as ClassLoader?).use { fs ->
                Files.walk(fs.getPath("/")).forEach { path ->
                    if (Files.isRegularFile(path))
                        task.patchFile(path)
                }
            }
        }
    }
}

fun ShadowJar.kotlinRelocate(pattern: String, destination: String, configure: Action<SimpleRelocator>) {
    val delegate = SimpleRelocator(pattern, destination, ArrayList(), ArrayList())
    configure.execute(delegate)
    storeRelocationPath(pattern, destination)
    relocate(KotlinRelocator(delegate))
}

fun ShadowJar.kotlinRelocate(pattern: String, destination: String) {
    kotlinRelocate(pattern, destination) {}
}

fun ShadowJar.patchMetadata() {
    KotlinRelocator.patchMetadata(this)
}