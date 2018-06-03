package hohserg.thaumwandmodel.client.baked

import java.util
import java.util.Collections
import javax.vecmath.Matrix4f

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model._
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.model.IPerspectiveAwareModel
import net.minecraftforge.common.model.TRSRTransformation
import org.apache.commons.lang3.tuple.Pair




class WandModel(baseModel: IBakedModel) extends IPerspectiveAwareModel {

  override def getParticleTexture: TextureAtlasSprite =
    null
  //baseModel.getParticleTexture


  override def getQuads(state: IBlockState, side: EnumFacing, rand: Long): util.List[BakedQuad] =
    Collections.emptyList()
  //baseModel.getQuads(state, side, rand)


  override def isAmbientOcclusion: Boolean =
    false
    //baseModel.isAmbientOcclusion

  override def isGui3d: Boolean =
    true
  //baseModel.isGui3d

  override def isBuiltInRenderer = false

  override def getItemCameraTransforms: ItemCameraTransforms = ItemCameraTransforms.DEFAULT


  override def getOverrides: ItemOverrideList = WandItemOverrideList

  override def handlePerspective(cameraTransformType: ItemCameraTransforms.TransformType): Pair[_ <: IBakedModel, Matrix4f] = {
    baseModel match {
      case model: IPerspectiveAwareModel =>
        val matrix4f = model.handlePerspective(cameraTransformType).getRight
        Pair.of(this, matrix4f)
      case _ =>
        val itemCameraTransforms = baseModel.getItemCameraTransforms
        val itemTransformVec3f = itemCameraTransforms.getTransform(cameraTransformType)
        val tr = new TRSRTransformation(itemTransformVec3f)
        val mat: Matrix4f = Option(tr.getMatrix).orNull

        Pair.of(this, mat)
    }
  }
}

