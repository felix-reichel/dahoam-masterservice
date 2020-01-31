package at.htl.mirrorhome.mirror

/**
 * Enum which describes different possible mirror states
 */
enum class MirrorState {
	/**
	 *	The Mirror is executing commands of a known user
	 */
	EXECUTING_IN_USER_MODE,
	/**
	 * The Mirror is executing a restricted command set for a unknown user
	 */
	EXECUTING_IN_SYSTEM_MODE,
	/**
	 * The Mirror is waiting for I/O (e.g. initial voice command)
	 */
	IDLE_WAITING_FOR_IO,
	/**
	 * The Mirror is running in idle
	 */
	IDLE
}
