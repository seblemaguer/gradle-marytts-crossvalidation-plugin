package de.dfki.mary.voicebuilding.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import marytts.*
import java.io.ByteArrayOutputStream

class RunCrossvalidation extends DefaultTask {

    @InputDirectory
    File srcDir = project.file("$project.buildDir/crossvalidation/output")

    @OutputDirectory
    File refDir = project.file("$project.buildDir/crossvalidation/reference")

    @OutputDirectory
    File destDir = project.file("$project.buildDir/crossvalidation")

    @OutputFile
    File allophonesFile = project.file("$project.buildDir/crossvalidation/allophones_en_US.xml")

    @OutputFile
    File outputFile = project.file("$project.buildDir/crossvalidation/rmslsfdistortion-results.txt")

    @Input
    Map<String, String> maryttsProperties = ['mary.base': "$project.buildDir/resources/legacy"]

    @TaskAction
    void run() {
        def allophonesString = this.class.getResourceAsStream('/de/dfki/mary/voicebuilding/templates/allophones_en_US.xml').text
        if (allophonesFile.length()==0) {
            allophonesFile << allophonesString }

        def stdout = new ByteArrayOutputStream()

        project.javaexec {
            classpath project.configurations.marytts
            main 'marytts.signalproc.analysis.distance.RmsLsfDistortionComputer'
            maryttsProperties.put('allophoneset', "$allophonesFile.path")
            systemProperties << maryttsProperties
            args refDir, srcDir
            standardOutput = stdout
        }
        outputFile.text = stdout
    }

}
