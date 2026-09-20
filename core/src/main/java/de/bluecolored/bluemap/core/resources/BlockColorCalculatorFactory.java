/*
 * This file is part of BlueMap, licensed under the MIT License (MIT).
 *
 * Copyright (c) Blue (Lukas Rieger) <https://bluecolored.de>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
