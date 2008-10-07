/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.aggregator;

import java.util.List;

import org.springframework.integration.message.Message;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.scheduling.TaskScheduler;
import org.springframework.util.Assert;

/**
 * An {@link AbstractMessageBarrierConsumer} that waits for a <em>complete</em>
 * group of {@link Message Messages} to arrive and then delegates to an
 * {@link Aggregator} to combine them into a single {@link Message}.
 * <p>
 * The default strategy for determining whether a group is complete is based on
 * the '<code>sequenceSize</code>' property of the header. Alternatively, a
 * custom implementation of the {@link CompletionStrategy} may be provided.
 * <p>
 * All considerations regarding <code>timeout</code> and grouping by '
 * <code>correlationId</code>' from {@link AbstractMessageBarrierConsumer}
 * apply here as well.
 * 
 * @author Mark Fisher
 * @author Marius Bogoevici
 */
public class AggregatorEndpoint extends AbstractMessageBarrierConsumer {

	private final Aggregator aggregator;

	private volatile CompletionStrategy completionStrategy = new SequenceSizeCompletionStrategy();

	/**
	 * Create an endpoint that delegates to the provided Aggregator to combine a
	 * group of messages into a single message. The executor will be used for
	 * scheduling a background maintenance thread. If <code>null</code>, a new
	 * single-threaded executor will be created.
	 */
	public AggregatorEndpoint(Aggregator aggregator, TaskScheduler executor) {
		super();
		Assert.notNull(aggregator, "'aggregator' must not be null");
		this.aggregator = aggregator;
	}

	public AggregatorEndpoint(Aggregator aggregator) {
		this(aggregator, null);
	}

	/**
	 * Strategy to determine whether the group of messages is complete.
	 */
	public void setCompletionStrategy(CompletionStrategy completionStrategy) {
		Assert.notNull(completionStrategy, "'completionStrategy' must not be null");
		this.completionStrategy = completionStrategy;
	}

	protected MessageBarrier createMessageBarrier() {
		return new AggregationBarrier(this.completionStrategy);
	}

	protected boolean isBarrierRemovable(Object correlationId, List<Message<?>> releasedMessages) {
		return releasedMessages != null && releasedMessages.size() > 0;
	}

	protected Message<?>[] processReleasedMessages(Object correlationId, List<Message<?>> messages) {
		Message<?> result = aggregator.aggregate(messages);
		if (result == null) {
			return new Message<?>[0];
		}
		if (result.getHeaders().getCorrelationId() == null) {
			result = MessageBuilder.fromMessage(result).setCorrelationId(correlationId).build();
		}
		return new Message<?>[] { result };
	}

}
