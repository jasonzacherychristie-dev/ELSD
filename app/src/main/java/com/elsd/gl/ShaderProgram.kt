package com.elsd.gl

import android.opengl.GLES30
import android.util.Log

class ShaderProgram(vertexSrc: String, fragmentSrc: String) {
    val id: Int

    init {
        val vs = compile(GLES30.GL_VERTEX_SHADER, vertexSrc)
        val fs = compile(GLES30.GL_FRAGMENT_SHADER, fragmentSrc)
        id = GLES30.glCreateProgram()
        GLES30.glAttachShader(id, vs)
        GLES30.glAttachShader(id, fs)
        GLES30.glLinkProgram(id)
        val link = IntArray(1)
        GLES30.glGetProgramiv(id, GLES30.GL_LINK_STATUS, link, 0)
        if (link[0] == 0) {
            val err = GLES30.glGetProgramInfoLog(id)
            GLES30.glDeleteProgram(id)
            throw RuntimeException("Program link failed: $err")
        }
        GLES30.glDeleteShader(vs)
        GLES30.glDeleteShader(fs)
    }

    fun use() = GLES30.glUseProgram(id)

    fun attrib(name: String): Int = GLES30.glGetAttribLocation(id, name)

    fun setUniform1f(name: String, v: Float) {
        GLES30.glUniform1f(loc(name), v)
    }

    fun setUniform2f(name: String, x: Float, y: Float) {
        GLES30.glUniform2f(loc(name), x, y)
    }

    fun setUniform1i(name: String, v: Int) {
        GLES30.glUniform1i(loc(name), v)
    }

    fun setUniform1fSafe(name: String, v: Float) {
        val l = loc(name)
        if (l >= 0) GLES30.glUniform1f(l, v)
    }

    private fun loc(name: String): Int = GLES30.glGetUniformLocation(id, name)

    fun release() {
        GLES30.glDeleteProgram(id)
    }

    private fun compile(type: Int, src: String): Int {
        val s = GLES30.glCreateShader(type)
        GLES30.glShaderSource(s, src)
        GLES30.glCompileShader(s)
        val ok = IntArray(1)
        GLES30.glGetShaderiv(s, GLES30.GL_COMPILE_STATUS, ok, 0)
        if (ok[0] == 0) {
            val err = GLES30.glGetShaderInfoLog(s)
            Log.e(TAG, "Shader compile error: $err\n$src")
            GLES30.glDeleteShader(s)
            throw RuntimeException("Shader compile failed: $err")
        }
        return s
    }

    companion object {
        private const val TAG = "ELSD.Shader"
    }
}
