attribute vec4 av_Position;//顶点位置
attribute vec2 af_Position;//纹理位置
attribute vec4 a_color;
varying vec4 f_color;

varying vec2 v_texPo;//纹理位置  与fragment_shader交互
uniform mat4 mat;
void main() {
    v_texPo = af_Position;
    f_color = a_color;
    gl_Position = mat * av_Position;
}
