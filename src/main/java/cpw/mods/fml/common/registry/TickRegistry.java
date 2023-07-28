/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common.registry;

import com.google.common.collect.Queues;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.SingleIntervalHandler;
import cpw.mods.fml.relauncher.Side;

import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicLong;

public class TickRegistry
{

    /**
     * We register our delegate here
     * @param handler
     */

    public static class TickQueueElement implements Comparable<TickQueueElement>
    {
        public TickQueueElement(final IScheduledTickHandler ticker, final long tickCounter)
        {
            this.ticker = ticker;
            update(tickCounter);
        }
        @Override
        public int compareTo(final TickQueueElement o)
        {
            return (int)(next - o.next);
        }

        public void update(final long tickCounter)
        {
            next = tickCounter + Math.max(ticker.nextTickSpacing(),1);
        }

        private long next;
        public IScheduledTickHandler ticker;

        public boolean scheduledNow(final long tickCounter)
        {
            return tickCounter >= next;
        }
    }

    private static PriorityQueue<TickQueueElement> clientTickHandlers = Queues.newPriorityQueue();
    private static PriorityQueue<TickQueueElement> serverTickHandlers = Queues.newPriorityQueue();

    private static AtomicLong clientTickCounter = new AtomicLong();
    private static AtomicLong serverTickCounter = new AtomicLong();

    public static void registerScheduledTickHandler(final IScheduledTickHandler handler, final Side side)
    {
        getQueue(side).add(new TickQueueElement(handler, getCounter(side).get()));
    }

    /**
     * @param side the side to get the tick queue for
     * @return the queue for the effective side
     */
    private static PriorityQueue<TickQueueElement> getQueue(final Side side)
    {
        return side.isClient() ? clientTickHandlers : serverTickHandlers;
    }

    private static AtomicLong getCounter(final Side side)
    {
        return side.isClient() ? clientTickCounter : serverTickCounter;
    }
    public static void registerTickHandler(final ITickHandler handler, final Side side)
    {
        registerScheduledTickHandler(new SingleIntervalHandler(handler), side);
    }

    public static void updateTickQueue(final List<IScheduledTickHandler> ticks, final Side side)
    {
        synchronized (ticks)
        {
            ticks.clear();
            final long tick = getCounter(side).incrementAndGet();
            final PriorityQueue<TickQueueElement> tickHandlers = getQueue(side);

            while (true)
            {
                if (tickHandlers.isEmpty() || !tickHandlers.peek().scheduledNow(tick))
                {
                    break;
                }
                final TickRegistry.TickQueueElement tickQueueElement  = tickHandlers.poll();
                tickQueueElement.update(tick);
                tickHandlers.offer(tickQueueElement);
                ticks.add(tickQueueElement.ticker);
            }
        }
    }

}
