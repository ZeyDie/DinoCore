package mcp.mobius.mobiuscore.profiler;

public class DummyProfiler implements IProfilerBase{
	@Override
	public void reset() {}
	
	@Override
	public void start() {}

	@Override
	public void stop() {}

	@Override
	public void start(final Object key) {}

	@Override
	public void stop(final Object key) {}

	@Override
	public void start(final Object key1, final Object key2) {}

	@Override
	public void stop(final Object key1, final Object key2) {}

	@Override
	public void start(final Object key1, final Object key2, final Object key3) {}

	@Override
	public void stop(final Object key1, final Object key2, final Object key3) {}
	
	@Override
	public void start(final Object key1, final Object key2, final Object key3, final Object key4) {}

	@Override
	public void stop(final Object key1, final Object key2, final Object key3, final Object key4) {}
}
