package jschutils

import com.jcraft.jsch.SftpProgressMonitor

class Monitor(val filename: String) : SftpProgressMonitor {
    private var startTime = 0L
    private var fileSize = 0L
    private var bytesTransferred = 0L

    var timeTaken = 0L
        private set

    var completed = false
        private set

    override fun init(op: Int, src: String, dest: String, max: Long) {
        startTime = System.currentTimeMillis()
        fileSize = max
    }

    override fun count(count: Long): Boolean {
        bytesTransferred += count
        timeTaken = System.currentTimeMillis() - startTime
        return true
    }

    override fun end() {
        completed = true
    }
}