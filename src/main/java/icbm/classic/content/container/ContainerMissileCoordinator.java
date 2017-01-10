package icbm.classic.content.container;

import icbm.classic.prefab.ContainerBase;
import icbm.classic.content.machines.coordinator.TileMissileCoordinator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerMissileCoordinator extends ContainerBase
{
    public ContainerMissileCoordinator(InventoryPlayer inventoryPlayer, TileMissileCoordinator tileEntity)
    {
        super(tileEntity);
        this.addSlotToContainer(new Slot(tileEntity, 0, 16, 41));
        this.addSlotToContainer(new Slot(tileEntity, 1, 136, 41));
        this.addPlayerInventory(inventoryPlayer.player);
    }
}
