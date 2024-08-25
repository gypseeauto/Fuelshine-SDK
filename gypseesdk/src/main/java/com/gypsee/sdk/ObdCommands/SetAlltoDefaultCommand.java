package com.gypsee.sdk.ObdCommands;

import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.ObdProtocolCommand;

public class SetAlltoDefaultCommand extends ObdProtocolCommand {

    /**
     * <p>Constructor for EchoOffCommand.</p>
     */
    public SetAlltoDefaultCommand() {
        super("AT D");
    }

    /**
     * <p>Constructor for EchoOffCommand.</p>
     *
     * @param other a {@link EchoOffCommand} object.
     */
    public SetAlltoDefaultCommand(EchoOffCommand other) {
        super(other);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormattedResult() {
        return getResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Set All to Default";
    }

}
