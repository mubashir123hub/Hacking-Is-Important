package net.lavaclient.client.event.events;

import net.lavaclient.client.event.Event;
import net.minecraft.network.Packet;

/**
 * Event fired when a packet is sent or received
 */
public class PacketEvent extends Event {
    private final Packet<?> packet;
    private final Direction direction;
    
    /**
     * Constructor
     * @param packet The packet
     * @param direction Packet direction
     */
    public PacketEvent(Packet<?> packet, Direction direction) {
        this.packet = packet;
        this.direction = direction;
    }
    
    /**
     * Gets the packet
     * @return The packet
     */
    public Packet<?> getPacket() {
        return packet;
    }
    
    /**
     * Gets the packet direction
     * @return Packet direction
     */
    public Direction getDirection() {
        return direction;
    }
    
    /**
     * Checks if the packet is incoming
     * @return Whether the packet is incoming
     */
    public boolean isIncoming() {
        return direction == Direction.INCOMING;
    }
    
    /**
     * Checks if the packet is outgoing
     * @return Whether the packet is outgoing
     */
    public boolean isOutgoing() {
        return direction == Direction.OUTGOING;
    }
    
    /**
     * Packet directions
     */
    public enum Direction {
        INCOMING,
        OUTGOING
    }
}
