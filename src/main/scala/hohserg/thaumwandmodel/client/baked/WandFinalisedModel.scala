package hohserg.thaumwandmodel.client.baked

import java.util
import javax.annotation.Nullable
import javax.vecmath.Matrix4f

import hohserg.thaumwandmodel.client.ItemRendererManager
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType
import net.minecraft.client.renderer.block.model._
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.model.IPerspectiveAwareModel
import net.minecraftforge.common.model.TRSRTransformation
import org.apache.commons.lang3.tuple.Pair

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.math.{cos, sin, toRadians}


class WandFinalisedModel(parentModel: IBakedModel,key:String) extends IPerspectiveAwareModel {
  private val textureRod = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(ItemRendererManager.textureRod.toString)
  private val textureCap = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(ItemRendererManager.textureCap.toString)

  private var memoization = new mutable.OpenHashMap[IBlockState,util.List[BakedQuad]]()

  private def quads(state: IBlockState): util.List[BakedQuad] = {
    memoization.getOrElse(state, {
      val combinedQuadsList = new util.ArrayList(parentModel.getQuads(state, null, 0))
      val fl = 0.2f

      combinedQuadsList.addAll(
        (
          Cube(-1, -1, -1, 2, 2, 2,textureCap).scale(1.2f,1,1.2f).scale(fl).toQuads++

          Cube(-1, -1, -1, 2, 2, 2,textureCap).scale(1.2f,1,1.2f).scale(fl).move(0, 20, 0).toQuads++

          Cube(-1, -1, -1, 2, 18, 2,textureRod).scale(fl).move(0, 2, 0).toQuads
        ).asJava
      )
      combinedQuadsList
    })
  }

  override def getQuads(@Nullable state: IBlockState, @Nullable side: EnumFacing, rand: Long): util.List[BakedQuad] = {
    if (side != null) return parentModel.getQuads(state, side, rand)
    quads(state)
  }

  override def isAmbientOcclusion: Boolean = parentModel.isAmbientOcclusion

  override def isGui3d: Boolean = parentModel.isGui3d

  override def isBuiltInRenderer: Boolean = parentModel.isBuiltInRenderer

  override def getParticleTexture: TextureAtlasSprite = parentModel.getParticleTexture

  override def getItemCameraTransforms: ItemCameraTransforms = parentModel.getItemCameraTransforms
  def identity: Matrix4f ={
    val m=new Matrix4f()
    m.setIdentity()
    m
  }
  def rotateY(i: Float):Matrix4f = {
    val m=identity
    m.m00=cos(toRadians(i)).toFloat
    m.m02 = sin(toRadians(i)).toFloat
    m.m20 = -sin(toRadians(i)).toFloat
    m.m22 = cos(toRadians(i)).toFloat
    m
  }

  def rotateZ(i: Float):Matrix4f = {
    val m=identity
    m.m00=cos(toRadians(i)).toFloat
    m.m01 = -sin(toRadians(i)).toFloat
    m.m10=sin(toRadians(i)).toFloat
    m.m11 = cos(toRadians(i)).toFloat
    m
  }

  def scale(d: Float):Matrix4f={
    val m=identity
    m.m00=d
    m.m11=d
    m.m22=d
    m
  }

  def move(d: Float, d1: Float, d2: Float): Matrix4f = {
    val m=identity
    m.m03=d
    m.m13=d1
    m.m23=d2
    m
  }

  override def handlePerspective(cameraTransformType: ItemCameraTransforms.TransformType): Pair[_ <: IBakedModel, Matrix4f] = {
    return parentModel match {
      case model: IPerspectiveAwareModel =>
        val matrix4f = model.handlePerspective(cameraTransformType).getRight
        Pair.of(this, matrix4f)
      case _ =>
        val itemCameraTransforms = parentModel.getItemCameraTransforms
        val itemTransformVec3f = itemCameraTransforms.getTransform(cameraTransformType)
        val tr = new TRSRTransformation(itemTransformVec3f)
        val mat: Matrix4f = Option(tr.getMatrix).orNull

        Pair.of(this, mat)
    }
    def matrixOrIdentity(o:Option[Matrix4f])=o.getOrElse(identity)
    val mat=parentModel match {
      case model: IPerspectiveAwareModel =>
        val matrix4f = model.handlePerspective(cameraTransformType).getRight
        matrixOrIdentity(Option(matrix4f))
      case _ =>
        val itemCameraTransforms = parentModel.getItemCameraTransforms
        val itemTransformVec3f = itemCameraTransforms.getTransform(cameraTransformType)
        val tr = new TRSRTransformation(itemTransformVec3f)
        var mat = Option(tr.getMatrix).orNull
        matrixOrIdentity(Option(mat))
    }

    cameraTransformType match {
      case TransformType.THIRD_PERSON_LEFT_HAND =>
        mat mul scale(0.4f)
        mat mul move(-0.2f,-1.9f,0.75f)

        mat mul move(-0.5f,0,-0.5f)
        mat mul rotateY(45)
        mat mul move(0.5f,0,0.5f)

      case TransformType.THIRD_PERSON_RIGHT_HAND =>
        mat mul scale(0.4f)
        mat mul move(0.5f,-1.9f,0.1f)

        mat mul move(-0.5f,0,-0.5f)
        mat mul rotateY(45)
        mat mul move(0.5f,0,0.5f)

      case TransformType.FIRST_PERSON_RIGHT_HAND =>
        mat mul rotateZ(-25)
        mat mul rotateY(-30)
        mat mul scale(0.15f)
        mat mul move(0,-2,2f)

      case TransformType.FIRST_PERSON_LEFT_HAND =>
        //mat mul rotateY(-30)
        mat mul scale(0.15f)
        mat mul move(-0.7f,-0.5f,2f)

      case TransformType.GROUND =>
        mat mul scale(0.15f)
        mat mul move(0.5f,-1f,0.5f)

      case TransformType.GUI =>
        mat mul rotateZ(45)
        mat mul rotateY(45)
        mat mul scale(0.2f)
        mat mul move(0.35f,-1.2f,0)
    }
    Pair.of(this,mat)
  }

    override def getOverrides = throw new UnsupportedOperationException("The finalised model does not have an override list")
  }