/*
 * BlueMap 5.7 binary compatibility adapter for legacy addons.
 */
package de.bluecolored.bluemap.core.resources;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;

/**
 * Compatibility facade for addons compiled against BlueMap 5.7.
 *
 * @deprecated Addons should use
 * {@link de.bluecolored.bluemap.core.map.hires.block.color.BlockColorCalculator}
 * and {@link ResourcePack#createBlockColorCalculator()} directly.
 */
@Deprecated
public class BlockColorCalculatorFactory {

    private final ResourcePack resourcePack;

    public BlockColorCalculatorFactory() {
        this.resourcePack = null;
    }

    public BlockColorCalculatorFactory(ResourcePack resourcePack) {
        this.resourcePack = resourcePack;
    }

    public de.bluecolored.bluemap.core.map.hires.block.color.BlockColorCalculator createCalculatorDelegate() {
        if (resourcePack == null) {
            return (block, state, target) -> target.set(1f, 1f, 1f, 1f, true);
        }
        return resourcePack.createBlockColorCalculator();
    }

    /**
     * Exact BlueMap 5.7 binary signature.
     */
    public BlockColorCalculatorFactory.BlockColorCalculator createCalculator() {
        return new BlockColorCalculatorFactory.BlockColorCalculator(createCalculatorDelegate());
    }

    /**
     * Exact legacy nested type used by 5.7 addons.
     */
    public class BlockColorCalculator {
        private final de.bluecolored.bluemap.core.map.hires.block.color.BlockColorCalculator delegate;

        private BlockColorCalculator(
                de.bluecolored.bluemap.core.map.hires.block.color.BlockColorCalculator delegate) {
            this.delegate = delegate;
        }

        public Color getBlockColor(BlockNeighborhood block, Color target) {
            return delegate.getBlockColor(block, target);
        }
    }
}
