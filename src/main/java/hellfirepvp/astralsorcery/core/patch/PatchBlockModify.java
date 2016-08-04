package hellfirepvp.astralsorcery.core.patch;

import hellfirepvp.astralsorcery.core.ClassPatch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatchSetBlock
 * Created by HellFirePvP
 * Date: 04.08.2016 / 00:39
 */
public class PatchBlockModify extends ClassPatch {

    public PatchBlockModify() {
        super("net.minecraft.world.chunk.Chunk");
    }

    @Override
    public boolean patch(ClassNode cn) {
        MethodNode mn = getMethod(cn, "setBlockState", "func_177436_a", "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;");
        if(mn != null) {
            boolean foundOne = false;
            for (int i = 0; i < mn.instructions.size(); i++) {
                AbstractInsnNode aNode = mn.instructions.get(i);
                if (aNode.getOpcode() == Opcodes.ARETURN) {
                    AbstractInsnNode prev = aNode.getPrevious();
                    if(prev instanceof VarInsnNode && prev.getOpcode() == Opcodes.ALOAD && ((VarInsnNode) prev).var == 8) {
                        foundOne = true;
                        mn.instructions.insertBefore(prev, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;"));
                        mn.instructions.insertBefore(prev, new TypeInsnNode(Opcodes.NEW, "hellfirepvp/astralsorcery/common/event/BlockModifyEvent"));
                        mn.instructions.insertBefore(prev, new InsnNode(Opcodes.DUP));
                        mn.instructions.insertBefore(prev, new VarInsnNode(Opcodes.ALOAD, 0)); //Chunk
                        mn.instructions.insertBefore(prev, new VarInsnNode(Opcodes.ALOAD, 1)); //Pos
                        mn.instructions.insertBefore(prev, new MethodInsnNode(Opcodes.INVOKESPECIAL, "hellfirepvp/astralsorcery/common/event/BlockModifyEvent", "<init>", "(Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;)V", false));
                        mn.instructions.insertBefore(prev, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false));
                        mn.instructions.insertBefore(prev, new InsnNode(Opcodes.POP));
                        break;
                    }
                }
            }
            return foundOne;
        }
        return false;
    }

}
