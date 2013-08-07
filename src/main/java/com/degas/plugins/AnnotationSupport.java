package com.degas.plugins;

import com.intellij.ant.Javac2;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.Project;

import com.intellij.ant.PrefixedPath;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

/**
 * Author: Ilya Varlamov aka privratnik (contact with me) degas.developer@gmail.com
 * Date: 07.08.13
 * Time: 16:04
 */

@Mojo(name = "run",
      defaultPhase = LifecyclePhase.PROCESS_CLASSES,
      threadSafe = true, requiresDependencyResolution = ResolutionScope.COMPILE)
public class AnnotationSupport extends AbstractMojo {

    /**
     * The directory for compiled classes.
     * */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true, readonly = true)
    private File outputDirectory;

    /**
     * The all properties Maven.
     * */
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject mavenProject;

    /**
     * The source directories containing the sources to be compiled.
     */
    @Parameter( defaultValue = "${project.compileSourceRoots}", readonly = true, required = true )
    private List<String> compileSourceRoots;

    /**
     * The encoding
     * */
    @Parameter(property = "encoding", defaultValue = "${project.build.sourceEncoding}")
    private String encoding;

    /**
     * Allows running the compiler in a separate process.
     * If false it uses the built in compiler, while if true it will use an executable.
     * */
    @Parameter(property = "maven.compiler.fork", defaultValue = "false")
    private boolean fork;

    /**
     * Initial size, in megabytes, of the memory allocation pool, ex. "64", "64m" if fork is set to true.
     * Important dependency in fork properties, need to set true
     * */
    @Parameter(property = "maven.compiler.meminitial")
    private String meminitial;

    /**
     * Sets the maximum size, in megabytes, of the memory allocation pool, ex. "128", "128m" if fork is set to true.
     * Important dependency in fork properties, need to set true
     * */
    @Parameter(property = "maven.compiler.maxmem")
    private String maxmem;

    /**
     * Sets the executable of the compiler to use when fork is true.
     * Important dependency in fork properties, need to set true
     * */
    @Parameter(property = "maven.compiler.executable")
    private String executable;

    /**
     * The -source argument for the Java compiler.
     * */
    @Parameter(property = "maven.compiler.source", defaultValue = "1.6")
    private String source;

    /**
     * The -target argument for the Java compiler.
     * */
    @Parameter(property = "maven.compiler.target", defaultValue = "1.6")
    private String target;

    /**
     * Set to true to include debugging information in the compiled class files.
     * */
    @Parameter(property = "maven.compiler.debug", defaultValue = "true")
    private boolean debug;

    /**
     * Set to true to show messages about what the compiler is doing.
     * */
    @Parameter(property = "maven.compiler.verbose", defaultValue = "false")
    private boolean verbose;

    /**
     * Set to true to optimize the compiled code using the compiler's optimization methods.
     * */
    @Parameter(property = "maven.compiler.optimize", defaultValue = "false")
    private boolean optimize;

    /**
     * Keyword list to be appended to the -g command-line switch. Legal values are none or a comma-separated
     * list of the following keywords: lines, vars, and source. If debug level is not specified, by default,
     * nothing will be appended to -g. If debug is not turned on, this attribute will be ignored.
     * */
    @Parameter(property = "maven.compiler.debuglevel")
    private String debuglevel;

    /**
     * The Maven basedir
     * */
    @Parameter(defaultValue = "${basedir}", required = true, readonly = true)
    private File basedir;

    public @NotNull String getEncoding() {
        return encoding;
    }

    public void setEncoding(@NotNull String encoding) {
        this.encoding = encoding;
    }

    public boolean isFork() {
        return fork;
    }

    public void setFork(boolean fork) {
        this.fork = fork;
    }

    public @NotNull String getMeminitial() {
        return meminitial;
    }

    public void setMeminitial(@NotNull String meminitial) {
        this.meminitial = meminitial;
    }

    public @NotNull String getMaxmem() {
        return maxmem;
    }

    public void setMaxmem(@NotNull String maxmem) {
        this.maxmem = maxmem;
    }

    public @NotNull String getExecutable() {
        return executable;
    }

    public void setExecutable(@NotNull String executable) {
        this.executable = executable;
    }

    public @NotNull String getSource() {
        return source;
    }

    public void setSource(@NotNull String source) {
        this.source = source;
    }

    public @NotNull String getTarget() {
        return target;
    }

    public void setTarget(@NotNull String target) {
        this.target = target;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isOptimize() {
        return optimize;
    }

    public void setOptimize(boolean optimize) {
        this.optimize = optimize;
    }

    public @NotNull String getDebuglevel() {
        return debuglevel;
    }

    public void setDebuglevel(@NotNull String debuglevel) {
        this.debuglevel = debuglevel;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        try {

            final Javac2 javac = new Javac2();
            javac.setIncludeantruntime(true);
            javac.setDestdir(outputDirectory);
            javac.setEncoding(encoding);
            javac.setFork(fork);
            javac.setExecutable(executable);
            javac.setMemoryInitialSize(meminitial);
            javac.setMemoryMaximumSize(maxmem);
            javac.setDebug(debug);
            javac.setVerbose(verbose);
            javac.setOptimize(optimize);
            javac.setSource(source);
            javac.setTarget(target);
            javac.setDebugLevel(debuglevel);

            Project project = new Project();
            project.setBaseDir(basedir);
            project.setJavaVersionProperty();
            javac.setProject(project);

            // ClassPath to sources
            for(String source : compileSourceRoots){
                javac.setSrcdir(new PrefixedPath(project, source));
            }

            // ClassPath to dependency
            for(String path : mavenProject.getCompileClasspathElements()){
                javac.setClasspath(new PrefixedPath(project, path));
            }

            // Task ant, run
            javac.execute();

            getLog().info("@NotNull assertions injection");

        } catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
