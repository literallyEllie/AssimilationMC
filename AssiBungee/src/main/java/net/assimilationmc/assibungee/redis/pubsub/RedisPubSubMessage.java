package net.assimilationmc.assibungee.redis.pubsub;

import com.google.common.base.Joiner;

public class RedisPubSubMessage {

    public static final String PARAM_SEPARATOR = ";######;";

    private String to, from, subject;
    private String[] args;

    /**
     * A message protocol which all redis messages should follow,
     * if they are to use this.
     *
     * @param to      The server its to.
     * @param from    The server its from.
     * @param subject The subject of the message.
     * @param args    The arguments of the pub-sub message.
     */
    public RedisPubSubMessage(String to, String from, String subject, String[] args) {
        this.to = to;
        this.from = from;
        this.subject = subject;
        this.args = args;
    }

    /**
     * Parse a pub-sub message from a received message from redis.
     *
     * @param input The received redis message.
     * @return the parsed redis message.
     * @throws MalformedRedisMessageException If it is invalid.
     */
    @Deprecated
    public static RedisPubSubMessage parse(String input) throws MalformedRedisMessageException {
        final String[] parameters = input.split(PARAM_SEPARATOR);

        if (parameters.length < 2)
            throw new MalformedRedisMessageException("Message only has " + parameters.length + " parameters!");

        String to = parameters[0];
        String from = parameters[1];
        String subject = parameters[2];

        String[] args = null;
        if (parameters.length > 3) {
            args = new String[parameters.length - 3];
            System.arraycopy(parameters, 3, args, 0, args.length);
        }

        return new RedisPubSubMessage(to, from, subject, args);
    }

    /**
     * @return the server the message is to.
     */
    public String getTo() {
        return to;
    }

    /**
     * @return the server the message is from.
     */
    public String getFrom() {
        return from;
    }

    /**
     * @return the subject of the message.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @return the arguments of the message.
     */
    public String[] getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return to + PARAM_SEPARATOR + from + PARAM_SEPARATOR + subject + PARAM_SEPARATOR + Joiner.on(PARAM_SEPARATOR).join(args);
    }

}
