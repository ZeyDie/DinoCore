
package ibxm;

public class Module {
	public String song_title;
	public boolean linear_periods, fast_volume_slides, pal;
	public int global_volume, channel_gain;
	public int default_speed, default_tempo;
	public int restart_sequence_index;
	
	private int[] initial_panning, sequence;
	private Pattern[] patterns;
	private Instrument[] instruments;
	
	private Pattern default_pattern;
	private Instrument default_instrument;
	
	public Module() {
		song_title = IBXM.VERSION;
		set_num_channels( 1 );
		set_sequence_length( 1 );
		set_num_patterns( 0 );
		set_num_instruments( 0 );
		default_pattern = new Pattern();
		default_instrument = new Instrument();
	}
	
	public int get_num_channels() {
		return initial_panning.length;
	}
	
	public void set_num_channels( int num_channels ) {
        int num_channels1 = num_channels;
        if( num_channels1 < 1 ) {
			num_channels1 = 1;
		}
		initial_panning = new int[num_channels1];
	}
	
	public int get_initial_panning(final int channel ) {
		int panning;
		panning = 128;
		if( channel >= 0 && channel < initial_panning.length ) {
			panning = initial_panning[ channel ];
		}
		return panning;
	}
	
	public void set_initial_panning(final int channel, final int panning ) {
		if( channel >= 0 && channel < initial_panning.length ) {
			initial_panning[ channel ] = panning;
		}
	}
	
	public int get_sequence_length() {
		return sequence.length;
	}
	
	public void set_sequence_length( int sequence_length ) {
        int sequence_length1 = sequence_length;
        if( sequence_length1 < 0 ) {
			sequence_length1 = 0;
		}
		sequence = new int[sequence_length1];
	}
	
	public void set_sequence(final int sequence_index, final int pattern_index ) {
		if( sequence_index >= 0 && sequence_index < sequence.length ) {
			sequence[ sequence_index ] = pattern_index;
		}
	}
	
	public int get_num_patterns() {
		return patterns.length;
	}
	
	public void set_num_patterns( int num_patterns ) {
        int num_patterns1 = num_patterns;
        if( num_patterns1 < 0 ) {
			num_patterns1 = 0;
		}
		patterns = new Pattern[num_patterns1];
	}

	public Pattern get_pattern_from_sequence(final int sequence_index ) {
		Pattern pattern;
		pattern = default_pattern;
		if( sequence_index >= 0 && sequence_index < sequence.length ) {
			pattern = get_pattern( sequence[ sequence_index ] );
		}
		return pattern;
	}

	public Pattern get_pattern(final int pattern_index ) {
		Pattern pattern;
		pattern = null;
		if( pattern_index >= 0 && pattern_index < patterns.length ) {
			pattern = patterns[ pattern_index ];
		}
		if( pattern == null ) {
			pattern = default_pattern;
		}
		return pattern;
	}

	public void set_pattern(final int pattern_index, final Pattern pattern ) {
		if( pattern_index >= 0 && pattern_index < patterns.length ) {
			patterns[ pattern_index ] = pattern;
		}
	}
	
	public int get_num_instruments() {
		return instruments.length;
	}
	
	public void set_num_instruments( int num_instruments ) {
        int num_instruments1 = num_instruments;
        if( num_instruments1 < 0 ) {
			num_instruments1 = 0;
		}
		instruments = new Instrument[num_instruments1];
	}
	
	public Instrument get_instrument(final int instrument_index ) {
		Instrument instrument;
		instrument = null;
		if( instrument_index > 0 && instrument_index <= instruments.length ) {
			instrument = instruments[ instrument_index - 1 ];
		}
		if( instrument == null ) {
			instrument = default_instrument;
		}
		return instrument;
	}
	
	public void set_instrument(final int instrument_index, final Instrument instrument ) {
		if( instrument_index > 0 && instrument_index <= instruments.length ) {
			instruments[ instrument_index - 1 ] = instrument;
		}
	}
}

