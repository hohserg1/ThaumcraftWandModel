package hohserg.thaumwandmodel.client.baked

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad

import scala.math._

object Cube{
  def apply(x: Float, y: Float, z: Float, w: Float, h: Float, d: Float,textureAtlasSprite: TextureAtlasSprite,color:(Float,Float,Float)=(1,1,1)): Cube =
    new Cube(x, y, z, w, h, d,0,0,0,1,1,1,textureAtlasSprite,color)

}

case class Cube(
                 x:Float,y:Float,z:Float
                ,w:Float,h:Float,d:Float
                ,cx:Float,cy:Float,cz:Float
                ,scaleX:Float,scaleY:Float,scaleZ:Float,
                 textureAtlasSprite: TextureAtlasSprite,color:(Float,Float,Float)) {

  def toQuads: List[BakedQuad] =
    List(//Создаем грани
      createQuad1((x, y, z), (x, y+h, z), (x+w, y+h, z), (x+w, y, z), textureAtlasSprite),
      createQuad1((x, y, z), (x+w, y, z), (x+w, y, z+d), (x, y, z+d), textureAtlasSprite),
      createQuad1((x, y+h, z),(x, y, z), (x, y, z+d), (x, y+h, z+d), textureAtlasSprite),
      createQuad1((x+w, y, z+d),(x+w, y+h, z+d), (x, y+h, z+d), (x, y, z+d), textureAtlasSprite),
      createQuad1((x+w, y+h, z+d), (x+w, y+h, z), (x, y+h, z), (x, y+h, z+d), textureAtlasSprite),
      createQuad1((x+w, y+h, z+d), (x+w, y, z+d), (x+w, y, z),(x+w, y+h, z), textureAtlasSprite)
    )

  //позволяет удобно задавать вершины в виде трех координат (x,y,z)
  implicit private def tuple2Vec[F:Numeric](t:(F ,F ,F )): Vec3d = {
    import Numeric.Implicits._
    new Vec3d(t._1.toDouble,t._2.toDouble,t._3.toDouble)
  }

  val format: VertexFormat = net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM

  private def putVertex(builder: UnpackedBakedQuad.Builder, normal: Vec3d, x: Double, y: Double, z: Double, u: Float, v: Float, sprite: TextureAtlasSprite,color:(Float,Float,Float)): Unit = {
    import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage._
    //format предполагает порядок следования свойств вершины, но мы об этом не задумываемся, код ниже определеяет порядок автоматически
    for (
      e <-0 until format.getElementCount
    ) {
      format.getElement(e).getUsage match {
        case POSITION =>
          builder.put(e, x.toFloat, y.toFloat, z.toFloat, 1.0f)
          
        case COLOR =>
          builder.put(e, color._1,color._2,color._3, 1.0f)
          
        case UV =>
          if (format.getElement(e).getIndex == 0) {
            val u1 = sprite.getInterpolatedU(u)
            val v1 = sprite.getInterpolatedV(v)
            builder.put(e, u1, v1, 0f, 1f)
            
          }
        case NORMAL =>
          builder.put(e, normal.xCoord.toFloat, normal.yCoord.toFloat, normal.zCoord.toFloat, 0f)
          
        case _ =>
          builder.put(e)
          
      }
    }
  }

  //масштаб, сжатие-расширение
  def extendedVectorScale(v1: Vec3d) = new Vec3d(v1.xCoord*scaleX,v1.yCoord*scaleY,v1.zCoord*scaleZ)

  private def createQuad1(v1: Vec3d, v2: Vec3d, v3: Vec3d, v4: Vec3d, sprite: TextureAtlasSprite, color:(Float,Float,Float)=color) = {
    val center=(cx*scaleX,cy*scaleY,cz*scaleZ)//смещение
    createQuad(extendedVectorScale(v1).add(center),extendedVectorScale(v2).add(center),extendedVectorScale(v3).add(center),extendedVectorScale(v4).add(center),sprite,color)
  }

  private def createQuad(v1: Vec3d, v2: Vec3d, v3: Vec3d, v4: Vec3d, sprite: TextureAtlasSprite,color:(Float,Float,Float)=color) = {
    //этот вектор определяет с какой стороны грань будет видна
    val normal:Vec3d = v1.subtract(v2).crossProduct(v3.subtract(v2))

    val builder = new UnpackedBakedQuad.Builder(format)//Вроде, самый простой способ создавать baked quad
    builder.setTexture(sprite)
    putVertex(builder, normal, v1.xCoord, v1.yCoord, v1.zCoord, 0, 0, sprite,color)
    putVertex(builder, normal, v2.xCoord, v2.yCoord, v2.zCoord, 0, 16, sprite,color)
    putVertex(builder, normal, v3.xCoord, v3.yCoord, v3.zCoord, 16, 16, sprite,color)
    putVertex(builder, normal, v4.xCoord, v4.yCoord, v4.zCoord, 16, 0, sprite,color)
    builder.build
  }

  //изменение модели
  def scale(sx:Float,sy:Float,sz:Float): Cube = copy(scaleX=scaleX*sx,scaleY=scaleY*sy,scaleZ=scaleZ*sz)
  def scale(s:Float): Cube = copy(scaleX=scaleX*s,scaleY=scaleY*s,scaleZ=scaleZ*s)
  def move(cx1:Float,cy1:Float,cz1:Float): Cube = copy(cx=cx+cx1,cy=cy+cy1,cz=cz+cz1)

}
