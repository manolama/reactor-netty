/*
 * Copyright (c) 2011-2019 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.netty;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.config.MeterFilterReply;
import reactor.util.Logger;
import reactor.util.Loggers;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class Metrics {
	// Metrics
	public static final String DATA_RECEIVED = ".data.received";

	public static final String DATA_SENT = ".data.sent";

	public static final String ERRORS = ".errors";

	public static final String TLS_HANDSHAKE_TIME = ".tls.handshake.time";

	public static final String CONNECT_TIME = ".connect.time";

	public static final String DATA_RECEIVED_TIME = ".data.received.time";

	public static final String DATA_SENT_TIME = ".data.sent.time";

	public static final String RESPONSE_TIME = ".response.time";


	// Tags
	public static final String REMOTE_ADDRESS = "remote.address";

	public static final String URI = "uri";

	public static final String STATUS = "status";

	public static final String METHOD = "method";

	public static final String SUCCESS = "SUCCESS";

	public static final String ERROR = "ERROR";


	static final int MAX_URI_TAGS = 100;


	@Nullable
	public static String formatSocketAddress(@Nullable SocketAddress socketAddress) {
		if (socketAddress != null) {
			if (socketAddress instanceof InetSocketAddress) {
				InetSocketAddress address = (InetSocketAddress) socketAddress;
				return address.getHostString() + ":" + address.getPort();
			}
			else {
				return socketAddress.toString();
			}
		}
		return null;
	}

	public static MeterFilter maxUriTagsMeterFilter(String meterNamePrefix) {
		return MeterFilter.maximumAllowableTags(meterNamePrefix, URI, MAX_URI_TAGS, new MaxUriTagsMeterFilter());
	}

	static final class MaxUriTagsMeterFilter implements MeterFilter {
		final AtomicBoolean logged = new AtomicBoolean(false);

		@Override
		public MeterFilterReply accept(Meter.Id id) {
			if (logger.isWarnEnabled() && logged.compareAndSet(false, true)) {
				logger.warn("Reached the maximum number of URI tags for {0}.", id.getName());
			}
			return MeterFilterReply.DENY;
		}

		static final Logger logger = Loggers.getLogger(MaxUriTagsMeterFilter.class);
	}
}