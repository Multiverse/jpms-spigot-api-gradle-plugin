package org.mvplugins.gradle.jpms

import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class SpigotModuleInfoTest extends Specification {
    def spigotVersion = System.getProperty('spigotVersion')

    @TempDir File testProjectDir
    File buildFile

    def setup() {
        File srcDir = new File(testProjectDir, 'src/main/java')
        srcDir.mkdirs()
        File moduleInfoFile = new File(srcDir, 'module-info.java')
        moduleInfoFile << '''
            module org.mvplugins.gradle.jpms {
                requires org.bukkit;
            }
        '''

        buildFile = new File(testProjectDir, 'build.gradle')
        buildFile << """
            plugins {
                id 'java'
                id 'org.mvplugins.jpms.spigot-api'
            }
            
            repositories {
                mavenCentral()
                maven {
                    url = uri('https://hub.spigotmc.org/nexus/content/repositories/snapshots/')
                }
            }
            
            dependencies {
                implementation 'org.spigotmc:spigot-api:${spigotVersion}'
            }
        """
    }

    def 'build is successful'() {
        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments('build')
            .withPluginClasspath()
            .build()

        then:
        result.task(":build").outcome == SUCCESS
    }
}
