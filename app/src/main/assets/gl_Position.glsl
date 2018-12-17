attribute vec3 vPosition;
attribute vec4 aColor;
varying vec4 vColor;
uniform mat4 mvp;
void main()
{
    vec4 pp = vec4(vPosition, 1.0);
    vColor = aColor;
    gl_Position = mvp * pp;
}
