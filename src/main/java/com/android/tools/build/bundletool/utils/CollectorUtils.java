/*
 * Copyright (C) 2018 The Android Open Source Project
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
 * limitations under the License
 */
package com.android.tools.build.bundletool.utils;

import static com.android.tools.build.bundletool.utils.ConcurrencyUtils.waitFor;
import static java.util.function.Function.identity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/** Utility class providing custom {@link Collector}s. */
public final class CollectorUtils {

  /**
   * Returns a {@code Collector} accumulating entries into an {@code ImmutableListMultimap}.
   *
   * <p>The keys of the entries are the result of applying the provided mapping function while the
   * values are accumulated in the encounter order of the stream.
   */
  public static <T, K extends Comparable<K>>
      Collector<T, ?, ImmutableListMultimap<K, T>> groupingBySortedKeys(
          Function<? super T, ? extends K> keyFunction) {
    return groupingBySortedKeys(keyFunction, identity());
  }

  /**
   * Returns a {@code Collector} accumulating entries into an {@code ImmutableListMultimap}.
   *
   * <p>The keys of the entries are the result of applying the provided key mapping function while
   * the values are generated by applying the value mapping function and accumulated in the
   * encounter order of the stream.
   */
  public static <T, K extends Comparable<K>, V>
      Collector<T, ?, ImmutableListMultimap<K, V>> groupingBySortedKeys(
          Function<? super T, ? extends K> keyFunction,
          Function<? super T, ? extends V> valueFunction) {
    return Collectors.collectingAndThen(
        Multimaps.toMultimap(
            keyFunction, valueFunction, MultimapBuilder.treeKeys().arrayListValues()::<K, V>build),
        ImmutableListMultimap::copyOf);
  }

  /**
   * Given a stream of {@link ListenableFuture}s, returns a {@link Collector} that waits
   * indefinitely for all the executions to complete, then returns an {@link ImmutableList} of the
   * result of the computations.
   */
  public static <T> Collector<ListenableFuture<T>, List<T>, ImmutableList<T>> waitForAll() {
    return Collector.of(
        ArrayList::new,
        (list, future) -> list.add(waitFor(future)),
        (list1, list2) -> {
          list1.addAll(list2);
          return list1;
        },
        ImmutableList::copyOf);
  }

  private CollectorUtils() {}
}
