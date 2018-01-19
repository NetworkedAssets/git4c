package com.networkedassets.git4c.utils

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.http.server.GitServlet
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File
import java.io.IOException

//Based on: https://github.com/centic9/jgit-cookbook/blob/master/httpserver/src/main/java/org/dstadler/jgit/server/Main.java
object TestJGitServer {

    val PORT = 53923

    /**
     * Creates server on port 53923
     * @return Location where repositories are located, <b>blocking</b> function to start server and function to stop server
     */
    fun create(): Triple<File, Function0<Unit>, Function0<Unit>> {

        val repository = createNewRepository()

        populateRepository(repository)

        // Create the JGit Servlet which handles the Git protocol
        val gs = GitServlet()
        gs.setRepositoryResolver { req, name ->
            repository.incrementOpen()
            repository
        }

        // start up the Servlet and start serving requests
        val server = configureAndStartHttpServer(gs)
        server.stopAtShutdown = true

        val f = { server.join() }

        val stop = { server.stop() }

        val location = repository.directory.parentFile

        return Triple(location, f, stop)

    }
    @Throws(Exception::class)
    private fun configureAndStartHttpServer(gs: GitServlet): Server {
        val server = Server(PORT)

        val handler = ServletHandler()
        server.setHandler(handler)

        val holder = ServletHolder(gs)

        handler.addServletWithMapping(holder, "/*")

        server.start()
        return server
    }

    @Throws(IOException::class, GitAPIException::class)
    private fun populateRepository(repository: Repository) {
        Git(repository).use { git ->
            val myfile = File(repository.getDirectory().getParent(), "testfile")
            if (!myfile.createNewFile()) {
                throw IOException("Could not create file " + myfile)
            }

            git.add().addFilepattern("testfile").call()

            git.commit().setMessage("Test-Checkin").call()
        }
    }

    @Throws(IOException::class)
    private fun createNewRepository(): Repository {
        // prepare a new folder
        val localPath = File.createTempFile("RemoteRepository", "")
        if (!localPath.delete()) {
            throw IOException("Could not delete temporary file " + localPath)
        }

        if (!localPath.mkdirs()) {
            throw IOException("Could not create directory " + localPath)
        }

        // create the directory
        val repository = FileRepositoryBuilder.create(File(localPath, ".git"))
        repository.create()

        return repository
    }

}