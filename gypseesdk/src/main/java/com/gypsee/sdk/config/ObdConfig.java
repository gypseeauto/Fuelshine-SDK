package com.gypsee.sdk.config;

import android.content.Context;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.control.DistanceMILOnCommand;
import com.github.pires.obd.commands.control.DtcNumberCommand;
import com.github.pires.obd.commands.control.ModuleVoltageCommand;
import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.control.VinCommand;
import com.github.pires.obd.commands.engine.LoadCommand;
import com.github.pires.obd.commands.engine.MassAirFlowCommand;
import com.github.pires.obd.commands.engine.OilTempCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.RuntimeCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.fuel.FindFuelTypeCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;

import java.util.ArrayList;

public final class ObdConfig {

    public static ArrayList<ObdCommand> getCommands(boolean isStarting, boolean isReset, boolean isAddVin, boolean isThirtySec, boolean isOneMinute, Context context) {
        ArrayList<ObdCommand> cmds = new ArrayList<>();

        if (isReset) {
            cmds.add(new ObdResetCommand());
            return cmds;
        }

        /*// Engine
        //Not necessary for long time
        // Fuel
        cmds.add(new ConsumptionRateCommand());
        //cmds.add(new AverageFuelEconomyObdCommand());
        //cmds.add(new FuelEconomyMAPObdCommand());
        //cmds.add(new FuelEconomyCommandedMAPObdCommand());

        cmds.add(new AirFuelRatioCommand());
        cmds.add(new WidebandAirFuelRatioCommand());

        // Pressure
        cmds.add(new BarometricPressureCommand());
        cmds.add(new FuelRailPressureCommand());*/
        // Misc

        if (isStarting) {
            cmds.add(new RPMCommand());
            return cmds;
        }

//        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(ConfigActivity.enable_speed_pid, false))
        cmds.add(new SpeedCommand());
        cmds.add(new RPMCommand());

        cmds.add(new LoadCommand());
        cmds.add(new EngineCoolantTemperatureCommand());
        cmds.add(new RuntimeCommand());
        cmds.add(new OilTempCommand());
        cmds.add(new MassAirFlowCommand());

        //Used to get Mileage
        cmds.add(new ThrottlePositionCommand());

        if (isThirtySec)
        //Need to put under 1 min
        {
            cmds.add(new TroubleCodesCommand());
//            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(ConfigActivity.enable_distance_post_cc, false))
//                cmds.add(new DistanceSinceCCCommand());
            cmds.add(new DistanceMILOnCommand());
            cmds.add(new ModuleVoltageCommand());
            cmds.add(new DtcNumberCommand());
            cmds.add(new FuelLevelCommand());
            cmds.add(new FindFuelTypeCommand());
        }

        if (isAddVin)
        //One time
        {
//            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(ConfigActivity.enable_distance_post_cc, false))
//                cmds.add(new DistanceSinceCCCommand());
            cmds.add(new VinCommand());
        }

       /* //park it
        //Below will be not necessary for now.
        cmds.add(new FuelPressureCommand());
        //cmds.add(new FuelEconomyCommand());

        // Not necessary for now
        // Control
        cmds.add(new EquivalentRatioCommand());
        cmds.add(new TimingAdvanceCommand());
        cmds.add(new FuelTrimCommand(FuelTrim.LONG_TERM_BANK_1));
        cmds.add(new FuelTrimCommand(FuelTrim.LONG_TERM_BANK_2));
        cmds.add(new FuelTrimCommand(FuelTrim.SHORT_TERM_BANK_1));
        cmds.add(new FuelTrimCommand(FuelTrim.SHORT_TERM_BANK_2));
        cmds.add(new AirIntakeTemperatureCommand());
        cmds.add(new AmbientAirTemperatureCommand());
        cmds.add(new IntakeManifoldPressureCommand());*/

        return cmds;
    }
}
