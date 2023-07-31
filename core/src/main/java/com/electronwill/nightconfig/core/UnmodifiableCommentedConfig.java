package com.electronwill.nightconfig.core;

import com.electronwill.nightconfig.core.utils.FakeUnmodifiableCommentedConfig;

import java.util.*;

import static com.electronwill.nightconfig.core.utils.StringUtils.split;

/**
 * An unmodifiable config that supports comments.
 *
 * @author TheElectronWill
 */
public interface UnmodifiableCommentedConfig extends UnmodifiableConfig {
	/**
	 * Gets a comment from the config.
	 *
	 * @param path the comment's path, each part separated by a dot. Example "a.b.c"
	 * @return the comment at the given path, or {@code null} if there is none.
	 */
	public default String getComment(String path) {
		return getComment(split(path, '.'));
	}

	/**
	 * Gets a comment from the config.
	 *
	 * @param path the comment's path, each element of the list is a different part of the path.
	 * @return the comment at the given path, or {@code null} if there is none.
	 */
	public String getComment(List<String> path);

	/**
	 * Gets an optional comment from the config.
	 *
	 * @param path the comment's path, each part separated by a dot. Example "a.b.c"
	 * @return an Optional containing the comment at the given path, or {@code Optional.empty()} if
	 * there is no such comment.
	 */
	public default Optional<String> getOptionalComment(String path) {
		return getOptionalComment(split(path, '.'));
	}

	/**
	 * Gets an optional comment from the config.
	 *
	 * @param path the comment's path, each element of the list is a different part of the path.
	 * @return an Optional containing the comment at the given path, or {@code Optional.empty()} if
	 * there is no such comment.
	 */
	public default Optional<String> getOptionalComment(List<String> path) {
		return Optional.ofNullable(getComment(path));
	}

	/**
	 * Checks if the config contains a comment at some path.
	 *
	 * @param path the path to check, each part separated by a dot. Example "a.b.c"
	 * @return {@code true} if the path is associated with a comment, {@code false} if it's not.
	 */
	public default boolean containsComment(String path) {
		return containsComment(split(path, '.'));
	}

	/**
	 * Checks if the config contains a comment at some path.
	 *
	 * @param path the path to check, each element of the list is a different part of the path.
	 * @return {@code true} if the path is associated with a comment, {@code false} if it's not.
	 */
	public boolean containsComment(List<String> path);

	/**
	 * Returns a Map view of the config's comments. If the config is unmodifiable then the returned
	 * map is unmodifiable too.
	 * <p>
	 * The comment map contains only the comments of the direct elements of the
	 * configuration, not the comments of their sub-elements.
	 *
	 * @return a Map view of the config's comments.
	 */
	public Map<String, String> commentMap();

	/**
	 * Returns a Map containing a deep copy of all the comments in the config.
	 *
	 * @return a Map containing the comments in the config.
	 */
	public default Map<String, CommentNode> getComments() {
		Map<String, CommentNode> map = new HashMap<>();
		getComments(map);
		return map;
	}

	/**
	 * Puts all the config's comments to the specified map.
	 *
	 * @param destination the map where to put the comments.
	 */
	public default void getComments(Map<String, CommentNode> destination) {
		for (Entry entry : entrySet()) {
			String key = entry.getKey();
			String comment = entry.getComment();
			Object value = entry.getValue();
			if (comment != null || value instanceof UnmodifiableCommentedConfig) {
				Map<String, CommentNode> children = (value instanceof UnmodifiableCommentedConfig)
													? ((UnmodifiableCommentedConfig)value).getComments()
													: null;
				CommentNode node = new CommentNode(comment, children);
				destination.put(key, node);
			}
		}
	}

	public final class CommentNode {
		private final String comment;
		private final Map<String, CommentNode> children;

		/**
		 * Creates a new CommentNode.
		 * <p>
		 * Note: The comment and children are never both null.
		 *
		 * @param comment  the comment, may be null
		 * @param children the children Map, may be null
		 */
		public CommentNode(String comment, Map<String, CommentNode> children) {
			if (comment == null && children == null) {
				throw new IllegalArgumentException("There is no point in creating a CommentNode "
												   + "if the comment AND the children are null.");
			}
			this.comment = comment;
			this.children = children;
		}

		/**
		 * @return the node's comment
		 */
		public String getComment() {
			return comment;
		}

		/**
		 * @return the Map of the node's children
		 */
		public Map<String, CommentNode> getChildren() {
			return children;
		}
	}

	@Override
	public Set<? extends Entry> entrySet();

	/**
	 * An unmodifiable commented config entry.
	 */
	public interface Entry extends UnmodifiableConfig.Entry {
		/**
		 * @return the entry's comment, may contain several lines
		 */
		public String getComment();
	}

	/**
	 * If the specified config is an instance of UnmodifiableCommentedConfig, returns it. Else,
	 * returns a "fake" UnmodifiableCommentedConfig instance with the same values (ie the valueMaps
	 * are equal) as the config. This fake UnmodifiableCommentedConfig doesn't actually store nor
	 * process comments, it just provides the methods of UnmodifiableCommentedConfig.
	 *
	 * @param config the config
	 * @return an UnmodifiableCommentedConfig instance backed by the specified config
	 */
	public static UnmodifiableCommentedConfig fake(UnmodifiableConfig config) {
		if (config instanceof UnmodifiableCommentedConfig) {
			return (UnmodifiableCommentedConfig)config;
		}
		return new FakeUnmodifiableCommentedConfig(config);
	}
}
