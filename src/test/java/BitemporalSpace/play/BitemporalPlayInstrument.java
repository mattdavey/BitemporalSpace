package BitemporalSpace.play;

import com.ervacon.bitemporal.Bitemporal;
import com.ervacon.bitemporal.BitemporalWrapper;
import org.joda.time.Interval;

public class BitemporalPlayInstrument extends BitemporalWrapper<PlayInstrument> {
    private PlayInstrument instrument;

    public BitemporalPlayInstrument(PlayInstrument value, Interval validityInterval) {
        super(value, validityInterval);
    }

    @Override
    public PlayInstrument getValue() {
        return instrument;
    }

    @Override
    public Bitemporal copyWith(Interval interval) {
        return new BitemporalPlayInstrument(instrument, interval);
    }
}
