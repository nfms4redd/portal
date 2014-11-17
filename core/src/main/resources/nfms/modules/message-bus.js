define([ "jquery" ], function($) {

	var messageBus = $({});

	/**
	 * Sends a message to the message bus. All the listeners of the event will
	 * get invoked and will receive the specified parameters in the callBack
	 * function.
	 * 
	 * @param {string}
	 *            name - The name of the message.
	 * @param {array}
	 *            parameters - An array containing the parameters that will be
	 *            sent to the listeners of the event.
	 */
	function send(name, parameters) {
		messageBus.trigger(name, parameters);
	}

	/**
	 * Registers a listener of the specified message. Whenever the specified
	 * message is sent, the specified callBack function will be invoked passing
	 * the parameters specified in the send invocation
	 * 
	 * @param {string}
	 *            name - The name of the message.
	 * @param {function}
	 *            callBack - A function receiving (a) an "event" object and (b)
	 *            the sequence of parameters specified in the call to send.
	 */
	function listen(name, callBack) {
		messageBus.bind(name, callBack);
	}

	return {
		send : send,
		listen : listen
	};
});