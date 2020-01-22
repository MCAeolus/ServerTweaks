package me.aeolus.servertweaks.util

import com.destroystokyo.paper.profile.PlayerProfile
import com.destroystokyo.paper.profile.ProfileProperty
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import me.aeolus.servertweaks.ServerTweaks
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

object MobHeads {

    fun getCorrespondingMobHead(ent : EntityType ) : ItemStack? {

        if(!ent.isAlive) return null

        return customPlayerHead((formatToMinecraftTextureLoc( when( ent ) {
            EntityType.ELDER_GUARDIAN -> "e92089618435a0ef63e95ee95a92b83073f8c33fa77dc5365199bad33b6256"

            EntityType.WITHER_SKELETON -> "7953b6c68448e7e6b6bf8fb273d7203acd8e1be19e81481ead51f45de59a8"
            EntityType.STRAY -> "78ddf76e555dd5c4aa8a0a5fc584520cd63d489c253de969f7f22f85a9a2d56"
            EntityType.HUSK -> "d674c63c8db5f4ca628d69a3b1f8a36e29d8fd775e1a6bdb6cabb4be4db121"
            EntityType.ZOMBIE_VILLAGER -> "fb552c90f212e855d12255d5cd62ed38b9cd7e30e73f0ea779d1764330e69264"
            EntityType.SKELETON_HORSE -> "47effce35132c86ff72bcae77dfbb1d22587e94df3cbc2570ed17cf8973a"
            EntityType.ZOMBIE_HORSE -> "d22950f2d3efddb18de86f8f55ac518dce73f12a6e0f8636d551d8eb480ceec"
            EntityType.DONKEY -> "2144bdad6bc18a3716b196dc4a4bd695265eccaadd0d945beb976443f82693b"
            EntityType.MULE -> "a0486a742e7dda0bae61ce2f55fa13527f1c3b334c57c034bb4cf132fb5f5f"
            EntityType.EVOKER -> "d954135dc82213978db478778ae1213591b93d228d36dd54f1ea1da48e7cba6"
            EntityType.VEX -> "c2ec5a516617ff1573cd2f9d5f3969f56d5575c4ff4efefabd2a18dc7ab98cd"
            EntityType.VINDICATOR -> "6deaec344ab095b48cead7527f7dee61b063ff791f76a8fa76642c8676e2173"
            EntityType.ILLUSIONER -> "512512e7d016a2343a7bff1a4cd15357ab851579f1389bd4e3a24cbeb88b"
            EntityType.CREEPER -> "f4254838c33ea227ffca223dddaabfe0b0215f70da649e944477f44370ca6952"
            EntityType.SKELETON -> "301268e9c492da1f0d88271cb492a4b302395f515a7bbf77f4a20b95fc02eb2"
            EntityType.SPIDER -> "cd541541daaff50896cd258bdbdd4cf80c3ba816735726078bfe393927e57f1"
            EntityType.GIANT -> "ec455b38368dae730ef1483c1df25cd87b41bee441ef3ab1f3c60f21bfe0e511"
            EntityType.ZOMBIE -> "56fc854bb84cf4b7697297973e02b79bc10698460b51a639c60e5e417734e11"
            EntityType.SLIME -> "895aeec6b842ada8669f846d65bc49762597824ab944f22f45bf3bbb941abe6c"
            EntityType.GHAST -> "8b6a72138d69fbbd2fea3fa251cabd87152e4f1c97e5f986bf685571db3cc0"
            EntityType.PIG_ZOMBIE -> "74e9c6e98582ffd8ff8feb3322cd1849c43fb16b158abb11ca7b42eda7743eb"
            EntityType.ENDERMAN -> "7a59bb0a7a32965b3d90d8eafa899d1835f424509eadd4e6b709ada50b9cf"
            EntityType.CAVE_SPIDER -> "41645dfd77d09923107b3496e94eeb5c30329f97efc96ed76e226e98224"
            EntityType.SILVERFISH -> "da91dab8391af5fda54acd2c0b18fbd819b865e1a8f1d623813fa761e924540"
            EntityType.BLAZE -> "b78ef2e4cf2c41a2d14bfde9caff10219f5b1bf5b35a49eb51c6467882cb5f0"
            EntityType.MAGMA_CUBE -> "38957d5023c937c4c41aa2412d43410bda23cf79a9f6ab36b76fef2d7c429"
            EntityType.ENDER_DRAGON -> "74ecc040785e54663e855ef0486da72154d69bb4b7424b7381ccf95b095a"
            EntityType.WITHER -> "cdf74e323ed41436965f5c57ddf2815d5332fe999e68fbb9d6cf5c8bd4139f"
            EntityType.BAT -> "9e99deef919db66ac2bd28d6302756ccd57c7f8b12b9dca8f41c3e0a04ac1cc"
            EntityType.WITCH -> "20e13d18474fc94ed55aeb7069566e4687d773dac16f4c3f8722fc95bf9f2dfa"
            EntityType.ENDERMITE -> "5a1a0831aa03afb4212adcbb24e5dfaa7f476a1173fce259ef75a85855"
            EntityType.GUARDIAN -> "a0bf34a71e7715b6ba52d5dd1bae5cb85f773dc9b0d457b4bfc5f9dd3cc7c94"
            EntityType.SHULKER -> "1e73832e272f8844c476846bc424a3432fb698c58e6ef2a9871c7d29aeea7"
            EntityType.PIG -> "621668ef7cb79dd9c22ce3d1f3f4cb6e2559893b6df4a469514e667c16aa4"
            EntityType.SHEEP -> "f31f9ccc6b3e32ecf13b8a11ac29cd33d18c95fc73db8a66c5d657ccb8be70"
            EntityType.COW -> "5d6c6eda942f7f5f71c3161c7306f4aed307d82895f9d2b07ab4525718edc5"
            EntityType.CHICKEN -> "1638469a599ceef7207537603248a9ab11ff591fd378bea4735b346a7fae893"
            EntityType.SQUID -> "01433be242366af126da434b8735df1eb5b3cb2cede39145974e9c483607bac"
            EntityType.WOLF -> "69d1d3113ec43ac2961dd59f28175fb4718873c6c448dfca8722317d67"
            EntityType.MUSHROOM_COW -> "2b52841f2fd589e0bc84cbabf9e1c27cb70cac98f8d6b3dd065e55a4dcb70d77"
            EntityType.SNOWMAN -> "11136616d8c4a87a54ce78a97b551610c2b2c8f6d410bc38b858f974b113b208"
            EntityType.OCELOT -> "5657cd5c2989ff97570fec4ddcdc6926a68a3393250c1be1f0b114a1db1"
            EntityType.IRON_GOLEM -> "89091d79ea0f59ef7ef94d7bba6e5f17f2f7d4572c44f90f76c4819a714"
            EntityType.HORSE -> "628d1ab4be1e28b7b461fdea46381ac363a7e5c3591c9e5d2683fbe1ec9fcd3"
            EntityType.RABBIT -> "ffecc6b5e6ea5ced74c46e7627be3f0826327fba26386c6cc7863372e9bc"
            EntityType.POLAR_BEAR -> "cd5d60a4d70ec136a658507ce82e3443cdaa3958d7fca3d9376517c7db4e695d"
            EntityType.LLAMA -> "818cd457fbaf327fa39f10b5b36166fd018264036865164c02d9e5ff53f45"
            EntityType.PARROT -> "a4ba8d66fecb1992e94b8687d6ab4a5320ab7594ac194a2615ed4df818edbc3"
            EntityType.VILLAGER -> "822d8e751c8f2fd4c8942c44bdb2f5ca4d8ae8e575ed3eb34c18a86e93b"
            EntityType.TURTLE -> "0a4050e7aacc4539202658fdc339dd182d7e322f9fbcc4d5f99b5718a"
            EntityType.PHANTOM -> "7e95153ec23284b283f00d19d29756f244313a061b70ac03b97d236ee57bd982"
            EntityType.COD -> "7892d7dd6aadf35f86da27fb63da4edda211df96d2829f691462a4fb1cab0"
            EntityType.SALMON -> "8aeb21a25e46806ce8537fbd6668281cf176ceafe95af90e94a5fd84924878"
            EntityType.PUFFERFISH -> "17152876bc3a96dd2a2299245edb3beef647c8a56ac8853a687c3e7b5d8bb"
            EntityType.TROPICAL_FISH -> "12510b301b088638ec5c8747e2d754418cb747a5ce7022c9c712ecbdc5f6f065"
            EntityType.DROWNED -> "c3f7ccf61dbc3f9fe9a6333cde0c0e14399eb2eea71d34cf223b3ace22051"
            EntityType.DOLPHIN -> "8e9688b950d880b55b7aa2cfcd76e5a0fa94aac6d16f78e833f7443ea29fed3"
            EntityType.CAT -> "4b432b7c8468cf6892868d863d38dd9e6b4cc0555768b8e07137f878f33e5b"
            EntityType.PANDA -> "8018a1771d69c11b8dad42cd310375ba2d827932b25ef357f7e572c1bd0f9"
            EntityType.PILLAGER -> "4aee6bb37cbfc92b0d86db5ada4790c64ff4468d68b84942fde04405e8ef5333"
            EntityType.RAVAGER -> "cd20bf52ec390a0799299184fc678bf84cf732bb1bd78fd1c4b441858f0235a8"
            EntityType.TRADER_LLAMA -> "8424780b3c5c5351cf49fb5bf41fcb289491df6c430683c84d7846188db4f84d"
            EntityType.WANDERING_TRADER -> "5f1379a82290d7abe1efaabbc70710ff2ec02dd34ade386bc00c930c461cf932"
            EntityType.FOX -> "d8954a42e69e0881ae6d24d4281459c144a0d5a968aed35d6d3d73a3c65d26a"
            else -> "e149fcd2d18aa9ab4799ce891be4fc96f18bee8ad6f7db717dcedbf65f797b0"
        })))

    }

    fun formatToMinecraftTextureLoc(code : String) = "http://textures.minecraft.net/texture/$code"

    fun customPlayerHead(skinLoc : String) : ItemStack {

        val profile = GameProfile(UUID.fromString("C56A4180-65AA-42EC-A945-5FD21DEC0538"), null)

        val encodedData = Base64.getEncoder().encode("{textures:{SKIN:{url:\"$skinLoc\"}}}".toByteArray())
        profile.properties.put("textures", Property("textures", String(encodedData)))
        val head = ItemStack(Material.PLAYER_HEAD)
        val headMeta = head.itemMeta as SkullMeta

        try {

            val profField = headMeta::class.java.getDeclaredField("profile")
            profField.isAccessible = true
            profField.set(headMeta, profile)

        } catch(e :Exception) {
            e.printStackTrace()
        }

        head.itemMeta = headMeta

        return head
    }

}