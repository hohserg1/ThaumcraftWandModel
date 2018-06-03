package hohserg.thaumwandmodel

import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTBase, NBTTagCompound, NBTTagList, NBTTagString}
case class NbtTag(nbt:NBTTagCompound)extends NbtHelper {
  override def get(key: String): Option[NBTBase] = Option(nbt.getTag(key))

  override def -=(key: String): NbtHelper = {
    nbt.removeTag(key)
    this
  }

  override def +=(key: String, value: NBTBase): NbtHelper = {
    nbt.setTag(key,value)
    this
  }

  override def hasKey(key: String): Boolean = nbt.hasKey(key)
}
object NbtEmpty extends NbtHelper {
  override def get(key: String): Option[NBTBase] = None

  override def -=(key: String): NbtHelper = this

  override def +=(key: String, value: NBTBase): NbtHelper = {
    val nbt=new NBTTagCompound()
      nbt.setTag(key,value)
    NbtTag(nbt)
  }

  override def hasKey(str: String): Boolean = false
}
sealed abstract class NbtHelper{
  def getList(key: String): List[String] = {
    get(key).map(_.asInstanceOf[NBTTagList]).map(list=>
      (for(i<-0 until list.tagCount()) yield list.get(i).asInstanceOf[NBTTagString].getString).toList
    ).getOrElse(Nil)
  }

  def addToList(key:String,value:NBTBase):NbtHelper={
    get(key).map{
      case list:NBTTagList=>
        list.appendTag(value)
        list
    }.map(+=(key,_)).orElse({
      +=(key,new NBTTagList())
      Option(addToList(key,value))
    }).getOrElse(this)
  }
  def hasKey(str: String): Boolean

  def unapply: Option[NBTTagCompound] = NbtHelper.unapply(this)

  def getString(key:String):Option[String]= get(key).flatMap{
    case e:NBTTagString=>Some(e.getString)
    case _=>None
  }
  def setString(key:String,value:String): NbtHelper = +=(key,new NBTTagString(value))

  def get(key:String):Option[NBTBase]
  def -=(key:String):NbtHelper
  def +=(key:String,value:NBTBase):NbtHelper

}

object NbtHelper {
  def unapply(arg: NbtHelper): Option[NBTTagCompound] = arg match{
    case NbtTag(nbt)=>Some(nbt)
    case NbtEmpty=>None
  }
  def apply(itemStack:ItemStack):NbtHelper=NbtHelper(itemStack.getTagCompound)
  def apply(nbt:NBTTagCompound):NbtHelper=
    if(nbt==null) NbtEmpty
    else NbtTag(nbt)


}
