#include "textStyle.h"
#include "textStyleBuilder.h"

#include "gl/shaderProgram.h"
#include "gl/mesh.h"
#include "gl/renderState.h"
#include "gl/dynamicQuadMesh.h"
#include "labels/textLabels.h"
#include "text/fontContext.h"
#include "view/view.h"

namespace Tangram {

TextStyle::TextStyle(std::string _name, bool _sdf, Blending _blendMode, GLenum _drawMode) :
    Style(_name, _blendMode, _drawMode), m_sdf(_sdf),
    m_context(std::make_shared<FontContext>()) {}

TextStyle::~TextStyle() {}

void TextStyle::constructVertexLayout() {
    m_vertexLayout = std::shared_ptr<VertexLayout>(new VertexLayout({
        {"a_position", 2, GL_SHORT, false, 0},
        {"a_uv", 2, GL_UNSIGNED_SHORT, false, 0},
        {"a_color", 4, GL_UNSIGNED_BYTE, true, 0},
        {"a_stroke", 4, GL_UNSIGNED_BYTE, true, 0},
        {"a_screen_position", 2, GL_SHORT, false, 0},
        {"a_alpha", 1, GL_UNSIGNED_BYTE, true, 0},
        {"a_scale", 1, GL_UNSIGNED_BYTE, false, 0},
        {"a_rotation", 1, GL_SHORT, false, 0},
    }));
}

void TextStyle::constructShaderProgram() {
    std::string frag = m_sdf ? "shaders/sdf.fs" : "shaders/text.fs";

    std::string vertShaderSrcStr = stringFromFile("shaders/point.vs", PathType::internal);
    std::string fragShaderSrcStr = stringFromFile(frag.c_str(), PathType::internal);

    m_shaderProgram->setSourceStrings(fragShaderSrcStr, vertShaderSrcStr);

    std::string defines = "#define TANGRAM_TEXT\n";

    m_shaderProgram->addSourceBlock("defines", defines);
}

void TextStyle::onBeginUpdate() {
    for (size_t i = 0; i < m_meshes.size(); i++) {
        m_meshes[i]->clear();
    }

    // Ensure that meshes are available to push to
    // in labels::update()
    size_t s = m_context->glyphTextureCount();
    while (m_meshes.size() < s) {
        m_meshes.push_back(std::make_unique<DynamicQuadMesh<TextVertex>>(m_vertexLayout, GL_TRIANGLES));
    }
}

void TextStyle::onBeginFrame() {
    m_context->updateTextures();

    // Upload meshes
    for (size_t i = 0; i < m_meshes.size(); i++) {
        m_meshes[i]->upload();
    }
}

void TextStyle::onBeginDrawFrame(const View& _view, Scene& _scene) {

    Style::onBeginDrawFrame(_view, _scene);

    auto texUnit = RenderState::nextAvailableTextureUnit();

    m_shaderProgram->setUniformf(m_uMaxStrokeWidth, m_context->maxStrokeWidth());
    m_shaderProgram->setUniformf(m_uTexScaleFactor, glm::vec2(1.0f / GlyphTexture::size));
    m_shaderProgram->setUniformi(m_uTex, texUnit);
    m_shaderProgram->setUniformMatrix4f(m_uOrtho, _view.getOrthoViewportMatrix());

    if (m_sdf) {
        m_shaderProgram->setUniformi(m_uPass, 1);

        for (size_t i = 0; i < m_meshes.size(); i++) {
            if (m_meshes[i]->isReady()) {
                m_context->bindTexture(i, texUnit);
                m_meshes[i]->draw(*m_shaderProgram);
            }
        }
        m_shaderProgram->setUniformi(m_uPass, 0);
    }

    for (size_t i = 0; i < m_meshes.size(); i++) {
        if (m_meshes[i]->isReady()) {
            m_context->bindTexture(i, texUnit);
            m_meshes[i]->draw(*m_shaderProgram);
        }
    }
}

std::unique_ptr<StyleBuilder> TextStyle::createBuilder() const {
    return std::make_unique<TextStyleBuilder>(*this);
}

}
