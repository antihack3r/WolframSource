/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class AdvancementCommand
extends CommandBase {
    @Override
    public String getCommandName() {
        return "advancement";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.advancement.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            throw new WrongUsageException("commands.advancement.usage", new Object[0]);
        }
        ActionType advancementcommand$actiontype = ActionType.func_193536_a(args[0]);
        if (advancementcommand$actiontype != null) {
            if (args.length < 3) {
                throw advancementcommand$actiontype.func_193534_a();
            }
            EntityPlayerMP entityplayermp = AdvancementCommand.getPlayer(server, sender, args[1]);
            Mode advancementcommand$actiontype$mode = Mode.func_193547_a(args[2]);
            if (advancementcommand$actiontype$mode == null) {
                throw advancementcommand$actiontype.func_193534_a();
            }
            this.func_193516_a(server, sender, args, entityplayermp, advancementcommand$actiontype, advancementcommand$actiontype$mode);
        } else {
            if (!"test".equals(args[0])) {
                throw new WrongUsageException("commands.advancement.usage", new Object[0]);
            }
            if (args.length == 3) {
                this.func_192552_c(sender, AdvancementCommand.getPlayer(server, sender, args[1]), AdvancementCommand.func_192551_a(server, args[2]));
            } else {
                if (args.length != 4) {
                    throw new WrongUsageException("commands.advancement.test.usage", new Object[0]);
                }
                this.func_192554_c(sender, AdvancementCommand.getPlayer(server, sender, args[1]), AdvancementCommand.func_192551_a(server, args[2]), args[3]);
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void func_193516_a(MinecraftServer p_193516_1_, ICommandSender p_193516_2_, String[] p_193516_3_, EntityPlayerMP p_193516_4_, ActionType p_193516_5_, Mode p_193516_6_) throws CommandException {
        if (p_193516_6_ == Mode.EVERYTHING) {
            if (p_193516_3_.length != 3) throw p_193516_6_.func_193544_a(p_193516_5_);
            int j = p_193516_5_.func_193532_a(p_193516_4_, p_193516_1_.func_191949_aK().func_192780_b());
            if (j == 0) {
                throw p_193516_6_.func_193543_a(p_193516_5_, p_193516_4_.getName());
            }
            p_193516_6_.func_193546_a(p_193516_2_, this, p_193516_5_, p_193516_4_.getName(), j);
            return;
        } else {
            if (p_193516_3_.length < 4) {
                throw p_193516_6_.func_193544_a(p_193516_5_);
            }
            Advancement advancement = AdvancementCommand.func_192551_a(p_193516_1_, p_193516_3_[3]);
            if (p_193516_6_ == Mode.ONLY && p_193516_3_.length == 5) {
                String s = p_193516_3_[4];
                if (!advancement.func_192073_f().keySet().contains(s)) {
                    throw new CommandException("commands.advancement.criterionNotFound", advancement.func_192067_g(), p_193516_3_[4]);
                }
                if (!p_193516_5_.func_193535_a(p_193516_4_, advancement, s)) {
                    throw new CommandException(String.valueOf(p_193516_5_.field_193541_d) + ".criterion.failed", advancement.func_192067_g(), p_193516_4_.getName(), s);
                }
                AdvancementCommand.notifyCommandListener(p_193516_2_, (ICommand)this, String.valueOf(p_193516_5_.field_193541_d) + ".criterion.success", advancement.func_192067_g(), p_193516_4_.getName(), s);
                return;
            } else {
                if (p_193516_3_.length != 4) {
                    throw p_193516_6_.func_193544_a(p_193516_5_);
                }
                List<Advancement> list = this.func_193514_a(advancement, p_193516_6_);
                int i = p_193516_5_.func_193532_a(p_193516_4_, list);
                if (i == 0) {
                    throw p_193516_6_.func_193543_a(p_193516_5_, advancement.func_192067_g(), p_193516_4_.getName());
                }
                p_193516_6_.func_193546_a(p_193516_2_, this, p_193516_5_, advancement.func_192067_g(), p_193516_4_.getName(), i);
            }
        }
    }

    private void func_193515_a(Advancement p_193515_1_, List<Advancement> p_193515_2_) {
        for (Advancement advancement : p_193515_1_.func_192069_e()) {
            p_193515_2_.add(advancement);
            this.func_193515_a(advancement, p_193515_2_);
        }
    }

    private List<Advancement> func_193514_a(Advancement p_193514_1_, Mode p_193514_2_) {
        ArrayList list = Lists.newArrayList();
        if (p_193514_2_.field_193555_h) {
            Advancement advancement = p_193514_1_.func_192070_b();
            while (advancement != null) {
                list.add(advancement);
                advancement = advancement.func_192070_b();
            }
        }
        list.add(p_193514_1_);
        if (p_193514_2_.field_193556_i) {
            this.func_193515_a(p_193514_1_, list);
        }
        return list;
    }

    private void func_192554_c(ICommandSender p_192554_1_, EntityPlayerMP p_192554_2_, Advancement p_192554_3_, String p_192554_4_) throws CommandException {
        PlayerAdvancements playeradvancements = p_192554_2_.func_192039_O();
        CriterionProgress criterionprogress = playeradvancements.func_192747_a(p_192554_3_).func_192106_c(p_192554_4_);
        if (criterionprogress == null) {
            throw new CommandException("commands.advancement.criterionNotFound", p_192554_3_.func_192067_g(), p_192554_4_);
        }
        if (!criterionprogress.func_192151_a()) {
            throw new CommandException("commands.advancement.test.criterion.notDone", p_192554_2_.getName(), p_192554_3_.func_192067_g(), p_192554_4_);
        }
        AdvancementCommand.notifyCommandListener(p_192554_1_, (ICommand)this, "commands.advancement.test.criterion.success", p_192554_2_.getName(), p_192554_3_.func_192067_g(), p_192554_4_);
    }

    private void func_192552_c(ICommandSender p_192552_1_, EntityPlayerMP p_192552_2_, Advancement p_192552_3_) throws CommandException {
        AdvancementProgress advancementprogress = p_192552_2_.func_192039_O().func_192747_a(p_192552_3_);
        if (!advancementprogress.func_192105_a()) {
            throw new CommandException("commands.advancement.test.advancement.notDone", p_192552_2_.getName(), p_192552_3_.func_192067_g());
        }
        AdvancementCommand.notifyCommandListener(p_192552_1_, (ICommand)this, "commands.advancement.test.advancement.success", p_192552_2_.getName(), p_192552_3_.func_192067_g());
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        if (args.length == 1) {
            return AdvancementCommand.getListOfStringsMatchingLastWord(args, "grant", "revoke", "test");
        }
        ActionType advancementcommand$actiontype = ActionType.func_193536_a(args[0]);
        if (advancementcommand$actiontype != null) {
            if (args.length == 2) {
                return AdvancementCommand.getListOfStringsMatchingLastWord(args, server.getAllUsernames());
            }
            if (args.length == 3) {
                return AdvancementCommand.getListOfStringsMatchingLastWord(args, Mode.field_193553_f);
            }
            Mode advancementcommand$actiontype$mode = Mode.func_193547_a(args[2]);
            if (advancementcommand$actiontype$mode != null && advancementcommand$actiontype$mode != Mode.EVERYTHING) {
                Advancement advancement;
                if (args.length == 4) {
                    return AdvancementCommand.getListOfStringsMatchingLastWord(args, this.func_193517_a(server));
                }
                if (args.length == 5 && advancementcommand$actiontype$mode == Mode.ONLY && (advancement = server.func_191949_aK().func_192778_a(new ResourceLocation(args[3]))) != null) {
                    return AdvancementCommand.getListOfStringsMatchingLastWord(args, advancement.func_192073_f().keySet());
                }
            }
        }
        if ("test".equals(args[0])) {
            Advancement advancement1;
            if (args.length == 2) {
                return AdvancementCommand.getListOfStringsMatchingLastWord(args, server.getAllUsernames());
            }
            if (args.length == 3) {
                return AdvancementCommand.getListOfStringsMatchingLastWord(args, this.func_193517_a(server));
            }
            if (args.length == 4 && (advancement1 = server.func_191949_aK().func_192778_a(new ResourceLocation(args[2]))) != null) {
                return AdvancementCommand.getListOfStringsMatchingLastWord(args, advancement1.func_192073_f().keySet());
            }
        }
        return Collections.emptyList();
    }

    private List<ResourceLocation> func_193517_a(MinecraftServer p_193517_1_) {
        ArrayList list = Lists.newArrayList();
        for (Advancement advancement : p_193517_1_.func_191949_aK().func_192780_b()) {
            list.add(advancement.func_192067_g());
        }
        return list;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return args.length > 1 && ("grant".equals(args[0]) || "revoke".equals(args[0]) || "test".equals(args[0])) && index == 1;
    }

    public static Advancement func_192551_a(MinecraftServer p_192551_0_, String p_192551_1_) throws CommandException {
        Advancement advancement = p_192551_0_.func_191949_aK().func_192778_a(new ResourceLocation(p_192551_1_));
        if (advancement == null) {
            throw new CommandException("commands.advancement.advancementNotFound", p_192551_1_);
        }
        return advancement;
    }

    static enum ActionType {
        GRANT("grant"){

            @Override
            protected boolean func_193537_a(EntityPlayerMP p_193537_1_, Advancement p_193537_2_) {
                AdvancementProgress advancementprogress = p_193537_1_.func_192039_O().func_192747_a(p_193537_2_);
                if (advancementprogress.func_192105_a()) {
                    return false;
                }
                for (String s : advancementprogress.func_192107_d()) {
                    p_193537_1_.func_192039_O().func_192750_a(p_193537_2_, s);
                }
                return true;
            }

            @Override
            protected boolean func_193535_a(EntityPlayerMP p_193535_1_, Advancement p_193535_2_, String p_193535_3_) {
                return p_193535_1_.func_192039_O().func_192750_a(p_193535_2_, p_193535_3_);
            }
        }
        ,
        REVOKE("revoke"){

            @Override
            protected boolean func_193537_a(EntityPlayerMP p_193537_1_, Advancement p_193537_2_) {
                AdvancementProgress advancementprogress = p_193537_1_.func_192039_O().func_192747_a(p_193537_2_);
                if (!advancementprogress.func_192108_b()) {
                    return false;
                }
                for (String s : advancementprogress.func_192102_e()) {
                    p_193537_1_.func_192039_O().func_192744_b(p_193537_2_, s);
                }
                return true;
            }

            @Override
            protected boolean func_193535_a(EntityPlayerMP p_193535_1_, Advancement p_193535_2_, String p_193535_3_) {
                return p_193535_1_.func_192039_O().func_192744_b(p_193535_2_, p_193535_3_);
            }
        };

        final String field_193540_c;
        final String field_193541_d;

        private ActionType(String p_i47557_3_) {
            this.field_193540_c = p_i47557_3_;
            this.field_193541_d = "commands.advancement." + p_i47557_3_;
        }

        @Nullable
        static ActionType func_193536_a(String p_193536_0_) {
            ActionType[] actionTypeArray = ActionType.values();
            int n = actionTypeArray.length;
            int n2 = 0;
            while (n2 < n) {
                ActionType advancementcommand$actiontype = actionTypeArray[n2];
                if (advancementcommand$actiontype.field_193540_c.equals(p_193536_0_)) {
                    return advancementcommand$actiontype;
                }
                ++n2;
            }
            return null;
        }

        CommandException func_193534_a() {
            return new CommandException(String.valueOf(this.field_193541_d) + ".usage", new Object[0]);
        }

        public int func_193532_a(EntityPlayerMP p_193532_1_, Iterable<Advancement> p_193532_2_) {
            int i = 0;
            for (Advancement advancement : p_193532_2_) {
                if (!this.func_193537_a(p_193532_1_, advancement)) continue;
                ++i;
            }
            return i;
        }

        protected abstract boolean func_193537_a(EntityPlayerMP var1, Advancement var2);

        protected abstract boolean func_193535_a(EntityPlayerMP var1, Advancement var2, String var3);
    }

    static enum Mode {
        ONLY("only", false, false),
        THROUGH("through", true, true),
        FROM("from", false, true),
        UNTIL("until", true, false),
        EVERYTHING("everything", true, true);

        static final String[] field_193553_f;
        final String field_193554_g;
        final boolean field_193555_h;
        final boolean field_193556_i;

        static {
            field_193553_f = new String[Mode.values().length];
            int i = 0;
            while (i < Mode.values().length) {
                Mode.field_193553_f[i] = Mode.values()[i].field_193554_g;
                ++i;
            }
        }

        private Mode(String p_i47556_3_, boolean p_i47556_4_, boolean p_i47556_5_) {
            this.field_193554_g = p_i47556_3_;
            this.field_193555_h = p_i47556_4_;
            this.field_193556_i = p_i47556_5_;
        }

        CommandException func_193543_a(ActionType p_193543_1_, Object ... p_193543_2_) {
            return new CommandException(String.valueOf(p_193543_1_.field_193541_d) + "." + this.field_193554_g + ".failed", p_193543_2_);
        }

        CommandException func_193544_a(ActionType p_193544_1_) {
            return new CommandException(String.valueOf(p_193544_1_.field_193541_d) + "." + this.field_193554_g + ".usage", new Object[0]);
        }

        void func_193546_a(ICommandSender p_193546_1_, AdvancementCommand p_193546_2_, ActionType p_193546_3_, Object ... p_193546_4_) {
            CommandBase.notifyCommandListener(p_193546_1_, (ICommand)p_193546_2_, String.valueOf(p_193546_3_.field_193541_d) + "." + this.field_193554_g + ".success", p_193546_4_);
        }

        @Nullable
        static Mode func_193547_a(String p_193547_0_) {
            Mode[] modeArray = Mode.values();
            int n = modeArray.length;
            int n2 = 0;
            while (n2 < n) {
                Mode advancementcommand$actiontype$mode = modeArray[n2];
                if (advancementcommand$actiontype$mode.field_193554_g.equals(p_193547_0_)) {
                    return advancementcommand$actiontype$mode;
                }
                ++n2;
            }
            return null;
        }
    }
}

