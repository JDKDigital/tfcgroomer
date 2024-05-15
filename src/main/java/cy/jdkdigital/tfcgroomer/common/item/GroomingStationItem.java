package cy.jdkdigital.tfcgroomer.common.item;

import cy.jdkdigital.tfcgroomer.Groomer;
import cy.jdkdigital.tfcgroomer.common.block.GroomingStation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GroomingStationItem extends BlockItem
{
    public GroomingStationItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        if (this.getBlock() instanceof GroomingStation groomingStation) {
            pTooltip.add(Component.translatable(Groomer.MODID + ".grooming_station.range", groomingStation.range));
        }
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }
}
