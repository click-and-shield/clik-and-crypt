package org.shadow.skriva;

import org.shadow.lib.exception.FatalRuntimeException;
import org.shadow.lib.exception.RecoverableRuntimeException;
import org.shadow.skriva.exception.RecoverableErrorCause;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RunVerifyConfiguration implements Runnable {

    private final Configuration configuration;

    public RunVerifyConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void run() throws FatalRuntimeException, RecoverableRuntimeException {
        verifyInputPath(configuration.getInput());
        configuration.setOutput(calculateOutputPath(configuration.getInput(), configuration.getAction()));
        verifyOutputPath(configuration.getOutput());
    }

    /**
     * Calculates the output path for a given input path according to the action to execute.
     *
     * @param inputPath the original input file path
     * @param action the action to execute on the input file (encryption or decryption).
     * @return the calculated output file path with modified filename
     * @apiNote Please note that this method can only be called once the application has been launched and
     *          the event loop has been started. Indeed, it may raise exceptions.
     */

    private static String calculateOutputPath(String inputPath, Action action) {
        String newFileName;
        final Path originalPath = Paths.get(inputPath);
        final Path parentPath = originalPath.getParent();
        final String fileName = originalPath.getFileName().toString();

        if (action == Action.Encrypt) {
            newFileName = fileName + ".rmb";
        } else {
            String suffix = fileName.substring(fileName.length() - 4).toLowerCase();
            if (suffix.equals(".rmb")) {
                newFileName = fileName.substring(0, fileName.length() - 4);
            } else {
                newFileName = fileName;
            }
        }

        return parentPath.resolve(newFileName).toString();
    }

    /**
     * Verifies that the input file specified in the configuration exists and is readable.
     *
     * @throws FatalRuntimeException if the input file does not exist or cannot be read.
     * @apiNote Please note that this method can only be called once the application has been launched and
     *          the event loop has been started. Indeed it may raise exceptions.
     */
    public static void verifyInputPath(String path) throws FatalRuntimeException {
        Path truePath;
        try {
            truePath = Paths.get(path);
        } catch (InvalidPathException e) {
            throw new FatalRuntimeException("the given input path is not valid",
                    String.format("input path: \"%s\"", path));
        }
        if (!Files.exists(truePath)) {
            throw new FatalRuntimeException("the input path does not exist",
                    String.format("input path: \"%s\"", path));
        }
        if (!Files.isRegularFile(truePath)) {
            throw new FatalRuntimeException("the input path does not reference a regular file",
                    String.format("input path: \"%s\"", path));
        }
        if (!Files.isReadable(truePath)) {
            throw new FatalRuntimeException("the input file is not readable",
                    String.format("input path: \"%s\"", path));
        }
    }

    /**
     * Verifies the validity of the specified output file path.
     *
     * @param path the output file path to be verified
     * @throws FatalRuntimeException if the provided path is invalid, the directory does not exist, or is not writable.
     * @throws org.shadow.skriva.exception.RecoverableRuntimeException if the output path already exists.
     * @apiNote Please note that this method can only be called once the application has been launched and
     *          the event loop has been started. Indeed it may raise exceptions.
     */

    public static void verifyOutputPath(String path) throws FatalRuntimeException, org.shadow.skriva.exception.RecoverableRuntimeException {
        Path truePath;

        try {
            truePath = Paths.get(path);
        } catch (InvalidPathException e) {
            throw new FatalRuntimeException("the output path is not valid",
                    String.format("output path: \"%s\"", path));
        }

        Path parentPath = truePath.getParent();
        if (!Files.isDirectory(parentPath)) {
            throw new FatalRuntimeException("the path to the output directory does not exist",
                    String.format("output directory: \"%s\"", parentPath));
        }
        if (!Files.isWritable(parentPath)) {
            throw new FatalRuntimeException("the output directory is not writable",
                    String.format("output directory: \"%s\"", parentPath));
        }
        if (Files.exists(truePath)) {
            throw new org.shadow.skriva.exception.RecoverableRuntimeException("the output file already exists",
                    String.format("output path: \"%s\"", path),
                    RecoverableErrorCause.OUTPUT_FILE_EXISTS);
        }
    }
}
