
package ibxm;

public class Envelope {
	public boolean sustain, looped;
	private int sustain_tick, loop_start_tick, loop_end_tick;
	private int[] ticks, ampls;

	public Envelope() {
		set_num_points( 1 );
	}

	public void set_num_points( int num_points ) {
        int num_points1 = num_points;
        int point;
		if( num_points1 <= 0 ) {
			num_points1 = 1;
		}
		ticks = new int[num_points1];
		ampls = new int[num_points1];
		set_point( 0, 0, 0, false );
	}

	/* When you set a point, all subsequent points are reset. */
	public void set_point(int point, int tick, final int ampl, final boolean delta ) {
        int tick1 = tick;
        int point1 = point;
        if( point1 >= 0 && point1 < ticks.length ) {
			if( point1 == 0 ) {
				tick1 = 0;
			}
			if( point1 > 0 ) {
				if( delta ) tick1 += ticks[ point1 - 1 ];
				if( tick1 <= ticks[ point1 - 1 ] ) {
					System.out.println( "Envelope: Point not valid (" + tick1 + " <= " + ticks[ point1 - 1 ] + ")");
					tick1 = ticks[ point1 - 1 ] + 1;
				}
			}
			ticks[point1] = tick1;
			ampls[point1] = ampl;
			point1 += 1;
			while( point1 < ticks.length ) {
				ticks[point1] = ticks[ point1 - 1 ] + 1;
				ampls[point1] = 0;
				point1 += 1;
			}
		}
	}

	public void set_sustain_point( int point ) {
        int point1 = point;
        if( point1 < 0 ) {
			point1 = 0;
		}
		if( point1 >= ticks.length ) {
			point1 = ticks.length - 1;
		}
		sustain_tick = ticks[point1];
	}

	public void set_loop_points( int start, int end ) {
        int start1 = start;
        int end1 = end;
        if( start1 < 0 ) {
			start1 = 0;
		}
		if( start1 >= ticks.length ) {
			start1 = ticks.length - 1;
		}
		if( end1 < start1 || end1 >= ticks.length ) {
			end1 = start1;
		}
		loop_start_tick = ticks[start1];
		loop_end_tick = ticks[end1];
	}

	public int next_tick(int tick, final boolean key_on ) {
        int tick1 = tick;
        tick1 = tick1 + 1;
		if( looped && tick1 >= loop_end_tick ) {
			tick1 = loop_start_tick;
		}
		if( sustain && key_on && tick1 >= sustain_tick ) {
			tick1 = sustain_tick;
		}
		return tick1;
	}

	public int calculate_ampl(final int tick ) {
		int idx;
        int point;
        int delta_t;
        final int delta_a;
        int ampl;
        ampl = ampls[ ticks.length - 1 ];
		if( tick < ticks[ ticks.length - 1 ] ) {
			point = 0;
			for( idx = 1; idx < ticks.length; idx++ ) {
				if( ticks[ idx ] <= tick ) {
					point = idx;
				}
			}
			delta_t = ticks[ point + 1 ] - ticks[ point ];
			delta_a = ampls[ point + 1 ] - ampls[ point ];
			ampl = ( delta_a << IBXM.FP_SHIFT ) / delta_t;
			ampl = ampl * ( tick - ticks[ point ] ) >> IBXM.FP_SHIFT;
			ampl = ampl + ampls[ point ];
		}
		return ampl;
	}
	
	public void dump() {
		int idx, tick;
		for( idx = 0; idx < ticks.length; idx++ ) {
			System.out.println( ticks[ idx ] + ", " + ampls[ idx ] );
		}
		for( tick = 0; tick < 222; tick++ ) {
			System.out.print( calculate_ampl( tick ) + ", " );
		}
	}
}

