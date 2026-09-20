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
package de.bluecolored.bluemap.common.web;

import com.flowpowered.math.vector.Vector2i;

import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.Map;

public final class TileUpdateTracker {

    private static final int MAX_UPDATES = 8192;

    private final ArrayDeque<TileUpdate> updates = new ArrayDeque<>();
    private long sequence;

    public synchronized void onTileUpdate(Vector2i tile, int lod) {
        TileUpdate update = new TileUpdate(++sequence, tile.getX(), tile.getY(), lod);
        updates.addLast(update);

        while (updates.size() > MAX_UPDATES) {
            updates.removeFirst();
        }
    }

    /**
     * Returns changes after {@code since}. When {@code since} is null, only the
     * current sequence is returned so a newly opened webapp does not replay old updates.
     */
    public synchronized String snapshot(Long since) {
        long currentSequence = sequence;

        if (since == null) {
            return "{\"sequence\":" + currentSequence + ",\"reset\":false,\"updates\":[]}";
        }

        boolean reset = since < 0 || since > currentSequence;
        if (!reset && !updates.isEmpty()) {
            long oldestAvailable = updates.getFirst().sequence;
            reset = since < oldestAvailable - 1;
        }

        if (reset) {
            return "{\"sequence\":" + currentSequence + ",\"reset\":true,\"updates\":[]}";
        }

        Map<String, TileUpdate> changed = new LinkedHashMap<>();
        for (TileUpdate update : updates) {
            if (update.sequence <= since) continue;

            String key = update.lod + ":" + update.x + ":" + update.y;
            changed.remove(key);
            changed.put(key, update);
        }

        StringBuilder json = new StringBuilder(64 + changed.size() * 48);
        json.append("{\"sequence\":").append(currentSequence)
                .append(",\"reset\":false,\"updates\":[");

        boolean first = true;
        for (TileUpdate update : changed.values()) {
            if (!first) json.append(',');
            first = false;

            json.append("{\"x\":").append(update.x)
                    .append(",\"y\":").append(update.y)
                    .append(",\"lod\":").append(update.lod)
                    .append('}');
        }

        return json.append("]}").toString();
    }

    private static final class TileUpdate {
        private final long sequence;
        private final int x;
        private final int y;
        private final int lod;

        private TileUpdate(long sequence, int x, int y, int lod) {
            this.sequence = sequence;
            this.x = x;
            this.y = y;
            this.lod = lod;
        }
    }
}
