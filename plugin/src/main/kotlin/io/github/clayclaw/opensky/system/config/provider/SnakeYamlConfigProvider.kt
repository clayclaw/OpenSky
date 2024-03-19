package io.github.clayclaw.opensky.system.config.provider

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor
import org.yaml.snakeyaml.nodes.Tag
import org.yaml.snakeyaml.representer.Representer
import java.io.File

class SnakeYamlConfigProvider(
    private val options: DumperOptions = DumperOptions().apply {
        defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        indent = 2
        isPrettyFlow = true
    }
): ConfigProvider {
    private val representer = Representer(options)
    private val yaml = Yaml(CustomClassLoaderConstructor(this.javaClass.classLoader, LoaderOptions()), representer, options)

    override fun <T> read(file: File, type: Class<T>): T {
        return file.inputStream().use {
            yaml.loadAs(it, type)
        }
    }
    override fun write(file: File, data: Any) {
        representer.addClassTag(data::class.java, Tag.MAP)
        file.writeText(yaml.dump(data))
    }
}