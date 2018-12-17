package com.example.lll.va.Task5;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SVRenderer implements GLSurfaceView.Renderer{
    private Triangle triangle;
    private Rectangle rectangle;
    private Cone cone;
    private int angle1 = 0;
    private int angle2 = 0;
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        triangle = new Triangle();
        rectangle = new Rectangle();
        cone = new Cone(2, Cone.SOLID_MODE);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        float ratio = (float) width / height;
        gl.glViewport(0,0,width,height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustumf(-ratio * 2, ratio * 2, -2, 2, 1,10);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glClearColor(255,255,255,0);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        drawCone(gl);
//        drawRectangle(gl);
//        drawTriangle(gl);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    private void drawTriangle(GL10 gl){
        gl.glLoadIdentity();
        gl.glTranslatef(1.0f, 0.0f,-2.0f);
        triangle.drawSelf(gl);
        gl.glLoadIdentity();
        gl.glTranslatef(-3.0f,2.0f,-5.0f);
        triangle.drawSelf(gl);
    }


    private void drawRectangle(GL10 gl){
        gl.glLoadIdentity();
        gl.glRotatef(angle1++, 0,0,1);
        gl.glTranslatef(0.0f, 1.0f,-2.0f);

        rectangle.drawSelf(gl);
    }

    private void drawCone(GL10 gl){
        gl.glLoadIdentity();
        GLU.gluLookAt(gl, 0,0,6,0f,0,0,0f,1f,0f);

        gl.glTranslatef(0.0f, 0.0f,-1.0f);

        gl.glRotatef(angle1++, 1,1,1);
        cone.drawSelf(gl);
    }
}
