package hohserg.thaumwandmodel.client.baked


import java.util.Collections

import net.minecraft.client.renderer.block.model.{IBakedModel, ItemOverrideList}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.world.World

import scala.collection.mutable


object WandItemOverrideList extends ItemOverrideList(Collections.emptyList()) {

  private val memoization = new mutable.OpenHashMap[String,WandFinalisedModel]()

  private def model(originalModel: IBakedModel, stack: ItemStack) = {
    val key=stack.getDisplayName
    memoization.getOrElse(key, {
      new WandFinalisedModel(originalModel, key)
    })
  }
  override def handleItemState(originalModel: IBakedModel, stack: ItemStack, world: World, entity: EntityLivingBase): IBakedModel = {
    model(originalModel, stack)
  }
}
