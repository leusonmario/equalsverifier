package nl.jqno.equalsverifier.internal.prefabvalues;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.prefabvalues.factories.*;
import nl.jqno.equalsverifier.internal.reflection.ConditionalInstantiator;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.StampedLock;
import java.util.regex.Pattern;

import static nl.jqno.equalsverifier.internal.reflection.Util.*;

/**
 * Creates instances of classes for use in a {@link PrefabValues} object.
 *
 * Contains hand-made instances of well-known Java API classes that cannot be
 * instantiated dynamically because of an internal infinite recursion of types,
 * or other issues.
 */
@SuppressFBWarnings(value = "SIC_INNER_SHOULD_BE_STATIC_ANON", justification = "That would be dozens of separate classes")
public final class JavaApiPrefabValues {
    private static final String JAVAFX_COLLECTIONS_PACKAGE = "javafx.collections.";
    private static final String JAVAFX_PROPERTY_PACKAGE = "javafx.beans.property.";
    private static final String GUAVA_PACKAGE = "com.google.common.collect.";
    private static final String JODA_PACKAGE = "org.joda.time.";

    private static final Comparator<Object> OBJECT_COMPARATOR = Comparator.comparingInt(Object::hashCode);

    private PrefabValues prefabValues;

    private enum Dummy { RED, BLACK }

    /**
     * Private constructor. Use {@link #addTo(PrefabValues)}.
     */
    private JavaApiPrefabValues(PrefabValues prefabValues) {
        this.prefabValues = prefabValues;
    }

    /**
     * Adds instances of Java API classes that cannot be instantiated
     * dynamically to {@code prefabValues}.
     *
     * @param prefabValues The instance of prefabValues that should
     *          contain the Java API instances.
     */
    public static void addTo(PrefabValues prefabValues) {
        new JavaApiPrefabValues(prefabValues).addJavaClasses();
    }

    private void addJavaClasses() {
        addPrimitiveClasses();
        addClasses();
        addCollection();
        addLists();
        addMaps();
        addSets();
        addQueues();
        addNioBuffers();
        addAwtClasses();
        addJava8ApiClasses();
        addJavaFxClasses();
        addJavaxApiClasses();
        addGoogleGuavaMultisetCollectionsClasses();
        addGoogleGuavaMultimapCollectionsClasses();
        addGoogleGuavaBiMapCollectionsClasses();
        addGoogleGuavaTableCollectionClasses();
        addGoogleGuavaRegularCollectionsClasses();
        addGoogleGuavaImmutableClasses();
        addNewGoogleGuavaClasses();
        addJodaTimeClasses();
    }

    @SuppressFBWarnings(
        value = {"DM_BOOLEAN_CTOR", "DM_NUMBER_CTOR", "DM_FP_NUMBER_CTOR", "DM_STRING_CTOR"},
        justification = "We really do need a separate instances with the same value")
    private void addPrimitiveClasses() {
        addValues(boolean.class, true, false, true);
        addValues(byte.class, (byte)1, (byte)2, (byte)1);
        addValues(char.class, 'a', 'b', 'a');
        addValues(double.class, 0.5D, 1.0D, 0.5D);
        addValues(float.class, 0.5F, 1.0F, 0.5F);
        addValues(int.class, 1, 2, 1);
        addValues(long.class, 1L, 2L, 1L);
        addValues(short.class, (short)1, (short)2, (short)1);

        addValues(Boolean.class, true, false, new Boolean(true));
        addValues(Byte.class, (byte)1, (byte)2, new Byte((byte)1));
        addValues(Character.class, 'a', 'b', new Character('a'));
        addValues(Double.class, 0.5D, 1.0D, new Double(0.5D));
        addValues(Float.class, 0.5F, 1.0F, new Float(0.5F));
        addValues(Integer.class, 1, 2, new Integer(1));
        addValues(Long.class, 1L, 2L, new Long(1L));
        addValues(Short.class, (short)1, (short)2, new Short((short)1));

        addValues(Object.class, new Object(), new Object(), new Object());
        addValues(Class.class, Class.class, Object.class, Class.class);
        addValues(String.class, "one", "two", new String("one"));
        addValues(Enum.class, Dummy.RED, Dummy.BLACK, Dummy.RED);
    }

    @SuppressFBWarnings(
        value = {"DMI_HARDCODED_ABSOLUTE_FILENAME", "DM_USELESS_THREAD"},
        justification = "We just need an instance of File and Thread; they're not for actual use.")
    private void addClasses() {
        addValues(BigDecimal.class, BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ZERO);
        addValues(BigInteger.class, BigInteger.ZERO, BigInteger.ONE, BigInteger.ZERO);
        addValues(Calendar.class, new GregorianCalendar(2010, 7, 4), new GregorianCalendar(2010, 7, 5), new GregorianCalendar(2010, 7, 4));
        addValues(Date.class, new Date(0), new Date(1), new Date(0));
        addValues(DateFormat.class, DateFormat.getTimeInstance(), DateFormat.getDateInstance(), DateFormat.getTimeInstance());
        addValues(File.class, new File(""), new File("/"), new File(""));
        addValues(Formatter.class, new Formatter(), new Formatter(), new Formatter());
        addValues(GregorianCalendar.class, new GregorianCalendar(2010, 7, 4), new GregorianCalendar(2010, 7, 5), new GregorianCalendar(2010, 7, 4));
        addValues(Locale.class, new Locale("nl"), new Locale("hu"), new Locale("nl"));
        addValues(Pattern.class, Pattern.compile("one"), Pattern.compile("two"), Pattern.compile("one"));
        addValues(SimpleDateFormat.class, new SimpleDateFormat("yMd"), new SimpleDateFormat("dMy"), new SimpleDateFormat("yMd"));
        addValues(Scanner.class, new Scanner("one"), new Scanner("two"), new Scanner("one"));
        addValues(TimeZone.class, TimeZone.getTimeZone("GMT+1"), TimeZone.getTimeZone("GMT+2"), TimeZone.getTimeZone("GMT+1"));
        addValues(Thread.class, new Thread("one"), new Thread("two"), new Thread("one"));
        addValues(Throwable.class, new Throwable(), new Throwable(), new Throwable());
        addValues(UUID.class, new UUID(0, -1), new UUID(1, 0), new UUID(0, -1));

        addFactory(ThreadLocal.class, new ThreadLocalFactory());

        // Constructing InetAddress reflectively, because it might throw an awkward exception otherwise.
        ConditionalInstantiator inetAddress = new ConditionalInstantiator("java.net.InetAddress");
        addValues(inetAddress.resolve(),
                inetAddress.callFactory("getByName", classes(String.class), objects("127.0.0.1")),
                inetAddress.callFactory("getByName", classes(String.class), objects("127.0.0.42")),
                inetAddress.callFactory("getByName", classes(String.class), objects("127.0.0.1")));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addCollection() {
        addFactory(Iterable.class, new CollectionFactory() {
            @Override public Collection createEmpty() { return new ArrayList<>(); }
        });
        addFactory(Collection.class, new CollectionFactory<Collection>() {
            @Override public Collection createEmpty() { return new ArrayList<>(); }
        });
    }

    @SuppressWarnings("rawtypes")
    private void addLists() {
        addFactory(List.class, new CollectionFactory<List>() {
            @Override public List createEmpty() { return new ArrayList<>(); }
        });
        addFactory(CopyOnWriteArrayList.class, new CollectionFactory<CopyOnWriteArrayList>() {
            @Override public CopyOnWriteArrayList createEmpty() { return new CopyOnWriteArrayList<>(); }
        });
        addFactory(LinkedList.class, new CollectionFactory<LinkedList>() {
            @Override public LinkedList createEmpty() { return new LinkedList<>(); }
        });
        addFactory(ArrayList.class, new CollectionFactory<ArrayList>() {
            @Override public ArrayList createEmpty() { return new ArrayList<>(); }
        });
        addFactory(Vector.class, new CollectionFactory<Vector>() {
            @Override public Vector createEmpty() { return new Vector<>(); }
        });
        addFactory(Stack.class, new CollectionFactory<Stack>() {
            @Override public Stack createEmpty() { return new Stack<>(); }
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addMaps() {
        addFactory(Map.class, new MapFactory<Map>() {
            @Override public Map createEmpty() { return new HashMap<>(); }
        });
        addFactory(SortedMap.class, new MapFactory<SortedMap>() {
            @Override public SortedMap createEmpty() { return new TreeMap<>(OBJECT_COMPARATOR); }
        });
        addFactory(NavigableMap.class, new MapFactory<NavigableMap>() {
            @Override public NavigableMap createEmpty() { return new TreeMap<>(OBJECT_COMPARATOR); }
        });
        addFactory(ConcurrentNavigableMap.class, new MapFactory<ConcurrentNavigableMap>() {
            @Override public ConcurrentNavigableMap createEmpty() { return new ConcurrentSkipListMap<>(OBJECT_COMPARATOR); }
        });
        addFactory(ConcurrentHashMap.class, new MapFactory<ConcurrentHashMap>() {
            @Override public ConcurrentHashMap createEmpty() { return new ConcurrentHashMap<>(); }
        });
        addFactory(HashMap.class, new MapFactory<HashMap>() {
            @Override public HashMap createEmpty() { return new HashMap<>(); }
        });
        addFactory(Hashtable.class, new MapFactory<Hashtable>() {
            @Override public Hashtable createEmpty() { return new Hashtable<>(); }
        });
        addFactory(LinkedHashMap.class, new MapFactory<LinkedHashMap>() {
            @Override public LinkedHashMap createEmpty() { return new LinkedHashMap<>(); }
        });
        addFactory(Properties.class, new MapFactory<Properties>() {
            @Override public Properties createEmpty() { return new Properties(); }
        });
        addFactory(TreeMap.class, new MapFactory<TreeMap>() {
            @Override public TreeMap createEmpty() { return new TreeMap<>(OBJECT_COMPARATOR); }
        });
        addFactory(WeakHashMap.class, new MapFactory<WeakHashMap>() {
            @Override public WeakHashMap createEmpty() { return new WeakHashMap<>(); }
        });
        addFactory(EnumMap.class, new ReflectiveEnumMapFactory());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addSets() {
        addFactory(Set.class, new CollectionFactory<Set>() {
            @Override public Set createEmpty() { return new HashSet(); }
        });
        addFactory(SortedSet.class, new CollectionFactory<SortedSet>() {
            @Override public SortedSet createEmpty() { return new TreeSet<>(OBJECT_COMPARATOR); }
        });
        addFactory(NavigableSet.class, new CollectionFactory<NavigableSet>() {
            @Override public NavigableSet createEmpty() { return new TreeSet<>(OBJECT_COMPARATOR); }
        });
        addFactory(CopyOnWriteArraySet.class, new CollectionFactory<CopyOnWriteArraySet>() {
            @Override public CopyOnWriteArraySet createEmpty() { return new CopyOnWriteArraySet<>(); }
        });
        addFactory(HashSet.class, new CollectionFactory<HashSet>() {
            @Override
            public HashSet createEmpty() { return new HashSet<>(); }
        });
        addFactory(TreeSet.class, new CollectionFactory<TreeSet>() {
            @Override public TreeSet createEmpty() { return new TreeSet<>(OBJECT_COMPARATOR); }
        });
        addFactory(EnumSet.class, new ReflectiveEnumSetFactory());

        BitSet redBitSet = new BitSet();
        BitSet blackBitSet = new BitSet();
        blackBitSet.set(0);
        BitSet redCopyBitSet = new BitSet();
        addValues(BitSet.class, redBitSet, blackBitSet, redCopyBitSet);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addQueues() {
        addFactory(Queue.class, new CollectionFactory<Queue>() {
            @Override public Queue createEmpty() { return new ArrayBlockingQueue<>(1); }
        });
        addFactory(BlockingQueue.class, new CollectionFactory<BlockingQueue>() {
            @Override public BlockingQueue createEmpty() { return new ArrayBlockingQueue<>(1); }
        });
        addFactory(Deque.class, new CollectionFactory<Deque>() {
            @Override public Deque createEmpty() { return new ArrayDeque<>(1); }
        });
        addFactory(BlockingDeque.class, new CollectionFactory<BlockingDeque>() {
            @Override public BlockingDeque createEmpty() { return new LinkedBlockingDeque<>(1); }
        });
        addFactory(ArrayBlockingQueue.class, new CollectionFactory<ArrayBlockingQueue>() {
            @Override public ArrayBlockingQueue createEmpty() { return new ArrayBlockingQueue<>(1); }
        });
        addFactory(ConcurrentLinkedQueue.class, new CollectionFactory<ConcurrentLinkedQueue>() {
            @Override public ConcurrentLinkedQueue createEmpty() { return new ConcurrentLinkedQueue<>(); }
        });
        addFactory(DelayQueue.class, new PrefabValueFactory<DelayQueue>() {
            @SuppressWarnings("unchecked")
            @Override
            public Tuple<DelayQueue> createValues(TypeTag tag, PrefabValues pf, LinkedHashSet<TypeTag> typeStack) {
                TypeTag delayed = new TypeTag(Delayed.class);
                DelayQueue red = new DelayQueue<>();
                red.add(pf.<Delayed>giveRed(delayed));
                DelayQueue black = new DelayQueue<>();
                black.add(pf.<Delayed>giveBlack(delayed));
                DelayQueue redCopy = new DelayQueue<>();
                redCopy.add(pf.<Delayed>giveRed(delayed));
                return new Tuple<>(red, black, redCopy);
            }
        });
        addFactory(LinkedBlockingQueue.class, new CollectionFactory<LinkedBlockingQueue>() {
            @Override public LinkedBlockingQueue createEmpty() { return new LinkedBlockingQueue<>(1); }
        });
        addFactory(PriorityBlockingQueue.class, new CollectionFactory<PriorityBlockingQueue>() {
            @Override public PriorityBlockingQueue createEmpty() { return new PriorityBlockingQueue<>(1, OBJECT_COMPARATOR); }
        });
        addValues(SynchronousQueue.class, new SynchronousQueue<>(), new SynchronousQueue<>(), new SynchronousQueue<>());
    }

    private void addNioBuffers() {
        addValues(Buffer.class,
                ByteBuffer.wrap(new byte[] { 0 }), ByteBuffer.wrap(new byte[] { 1 }), ByteBuffer.wrap(new byte[] { 0 }));
        addValues(ByteBuffer.class,
                ByteBuffer.wrap(new byte[] { 0 }), ByteBuffer.wrap(new byte[] { 1 }), ByteBuffer.wrap(new byte[] { 0 }));
        addValues(CharBuffer.class,
                CharBuffer.wrap("a"), CharBuffer.wrap("b"), CharBuffer.wrap("a"));
        addValues(DoubleBuffer.class,
                DoubleBuffer.wrap(new double[] { 0.0 }), DoubleBuffer.wrap(new double[] { 1.0 }), DoubleBuffer.wrap(new double[] { 0.0 }));
        addValues(FloatBuffer.class,
                FloatBuffer.wrap(new float[] { 0.0f }), FloatBuffer.wrap(new float[] { 1.0f }), FloatBuffer.wrap(new float[] { 0.0f }));
        addValues(IntBuffer.class,
                IntBuffer.wrap(new int[] { 0 }), IntBuffer.wrap(new int[] { 1 }), IntBuffer.wrap(new int[] { 0 }));
        addValues(LongBuffer.class,
                LongBuffer.wrap(new long[] { 0 }), LongBuffer.wrap(new long[] { 1 }), LongBuffer.wrap(new long[] { 0 }));
        addValues(ShortBuffer.class,
                ShortBuffer.wrap(new short[] { 0 }), ShortBuffer.wrap(new short[] { 1 }), ShortBuffer.wrap(new short[] { 0 }));
    }

    private void addAwtClasses() {
        prefabValues.addLazyFactory("java.awt.Color", new ReflectiveLazyConstantFactory<>("java.awt.Color", "RED", "BLACK"));
        prefabValues.addLazyFactory("java.awt.color.ColorSpace", new ReflectiveLazyAwtFactory<>("java.awt.color.ColorSpace"));
        prefabValues.addLazyFactory("java.awt.color.ICC_ColorSpace", new ReflectiveLazyAwtFactory<>("java.awt.color.ColorSpace"));
        prefabValues.addLazyFactory("java.awt.color.ICC_Profile", new ReflectiveLazyAwtFactory<>("java.awt.color.ICC_Profile"));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addJava8ApiClasses() {
        addValues(LocalDateTime.class, LocalDateTime.MIN, LocalDateTime.MAX, LocalDateTime.MIN);
        addValues(LocalDate.class, LocalDate.MIN, LocalDate.MAX, LocalDate.MIN);
        addValues(LocalTime.class, LocalTime.MIN, LocalTime.MAX, LocalDate.MIN);
        addValues(ZoneId.class, ZoneId.of("+1"), ZoneId.of("-10"), ZoneId.of("+1"));
        addValues(ZoneOffset.class, ZoneOffset.ofHours(1), ZoneOffset.ofHours(-1), ZoneOffset.ofHours(1));
        addValues(DateTimeFormatter.class, DateTimeFormatter.ISO_TIME, DateTimeFormatter.ISO_DATE, DateTimeFormatter.ISO_TIME);
        addValues(StampedLock.class, new StampedLock(), new StampedLock(), new StampedLock());

        addValues(ZonedDateTime.class,
            ZonedDateTime.parse("2017-12-13T10:15:30+01:00"),
            ZonedDateTime.parse("2016-11-12T09:14:29-01:00"),
            ZonedDateTime.parse("2017-12-13T10:15:30+01:00"));

        String optional = "java.util.Optional";
        addFactory(classForName(optional), new ReflectiveGenericContainerFactory(optional, "of", Object.class));

        ConditionalInstantiator completableFuture = new ConditionalInstantiator("java.util.concurrent.CompletableFuture");
        addValues(completableFuture.resolve(),
                completableFuture.instantiate(classes(), objects()),
                completableFuture.instantiate(classes(), objects()),
                completableFuture.instantiate(classes(), objects()));
    }

    private void addJavaFxClasses() {
        addJavaFxCollection("ObservableList", List.class, "observableList");
        addJavaFxCollection("ObservableMap", Map.class, "observableMap");
        addJavaFxCollection("ObservableSet", Set.class, "observableSet");
        addJavaFxProperty("BooleanProperty", "SimpleBooleanProperty", boolean.class);
        addJavaFxProperty("DoubleProperty", "SimpleDoubleProperty", double.class);
        addJavaFxProperty("FloatProperty", "SimpleFloatProperty", float.class);
        addJavaFxProperty("IntegerProperty", "SimpleIntegerProperty", int.class);
        addJavaFxProperty("ListProperty", "SimpleListProperty", classForName(JAVAFX_COLLECTIONS_PACKAGE + "ObservableList"));
        addJavaFxProperty("LongProperty", "SimpleLongProperty", long.class);
        addJavaFxProperty("MapProperty", "SimpleMapProperty", classForName(JAVAFX_COLLECTIONS_PACKAGE + "ObservableMap"));
        addJavaFxProperty("ObjectProperty", "SimpleObjectProperty", Object.class);
        addJavaFxProperty("SetProperty", "SimpleSetProperty", classForName(JAVAFX_COLLECTIONS_PACKAGE + "ObservableSet"));
        addJavaFxProperty("StringProperty", "SimpleStringProperty", String.class);
    }

    private void addJavaxApiClasses() {
        ConditionalInstantiator reference = new ConditionalInstantiator("javax.naming.Reference");
        addValues(reference.resolve(),
                reference.instantiate(classes(String.class), objects("one")),
                reference.instantiate(classes(String.class), objects("two")),
                reference.instantiate(classes(String.class), objects("one")));
    }

    private void addGoogleGuavaMultisetCollectionsClasses() {
        addNewGuavaCollection("Multiset", "HashMultiset");
        addNewGuavaCollection("SortedMultiset", "TreeMultiset", Comparator.class, OBJECT_COMPARATOR);
        addNewGuavaCollection("HashMultiset", "HashMultiset");
        addNewGuavaCollection("TreeMultiset", "TreeMultiset", Comparator.class, OBJECT_COMPARATOR);
        addNewGuavaCollection("LinkedHashMultiset", "LinkedHashMultiset");
        addNewGuavaCollection("ConcurrentHashMultiset", "ConcurrentHashMultiset");
        addCopiedGuavaCollection("EnumMultiset", Iterable.class, EnumSet.class, "create");
        addCopiedGuavaCollection("ImmutableMultiset", Iterable.class);
        addCopiedGuavaCollection("ImmutableSortedMultiset", classForName(GUAVA_PACKAGE + "SortedMultiset"), "copyOfSorted");
    }

    private void addGoogleGuavaMultimapCollectionsClasses() {
        addNewGuavaMap("Multimap", "ArrayListMultimap");
        addNewGuavaMap("ListMultimap", "ArrayListMultimap");
        addNewGuavaMap("SetMultimap", "HashMultimap");
        addNewGuavaMap("SortedSetMultimap", "TreeMultimap", OBJECT_COMPARATOR);
        addNewGuavaMap("ArrayListMultimap", "ArrayListMultimap");
        addNewGuavaMap("HashMultimap", "HashMultimap");
        addNewGuavaMap("LinkedListMultimap", "LinkedListMultimap");
        addNewGuavaMap("LinkedHashMultimap", "LinkedHashMultimap");
        addNewGuavaMap("TreeMultimap", "TreeMultimap", OBJECT_COMPARATOR);
        addCopiedGuavaCollection("ImmutableListMultimap", classForName(GUAVA_PACKAGE + "Multimap"));
        addCopiedGuavaCollection("ImmutableSetMultimap", classForName(GUAVA_PACKAGE + "Multimap"));

        ConditionalInstantiator ci = new ConditionalInstantiator(GUAVA_PACKAGE + "Multimap");
        addCopiedGuavaCollection("ImmutableMultimap", "ImmutableListMultimap", ci.resolve(), ci.resolve(), "copyOf");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addGoogleGuavaBiMapCollectionsClasses() {
        addNewGuavaMap("BiMap", "HashBiMap");
        addNewGuavaMap("HashBiMap", "HashBiMap");
        addCopiedGuavaCollection("EnumHashBiMap", Map.class, EnumMap.class, "create");
        addCopiedGuavaCollection("ImmutableBiMap", Map.class);
        addFactory(classForName(GUAVA_PACKAGE + "EnumBiMap"), new ReflectiveGuavaEnumBiMapFactory());
    }

    private void addGoogleGuavaTableCollectionClasses() {
        addNewGuavaTable("Table", "HashBasedTable");
        addNewGuavaTable("HashBasedTable", "HashBasedTable");
        addNewGuavaTable("TreeBasedTable", "TreeBasedTable", OBJECT_COMPARATOR);
        addCopiedGuavaCollection("ArrayTable", classForName(GUAVA_PACKAGE + "Table"), "create");
        addCopiedGuavaCollection("ImmutableTable", classForName(GUAVA_PACKAGE + "Table"));
    }

    private void addGoogleGuavaRegularCollectionsClasses() {
        addNewGuavaCollection("EvictingQueue", "EvictingQueue", int.class, 10);
        addNewGuavaCollection("MinMaxPriorityQueue", "MinMaxPriorityQueue");

        ConditionalInstantiator range = new ConditionalInstantiator(GUAVA_PACKAGE + "Range");
        ConditionalInstantiator rangeSet = new ConditionalInstantiator(GUAVA_PACKAGE + "RangeSet");
        ConditionalInstantiator immutableRangeSet = new ConditionalInstantiator(GUAVA_PACKAGE + "ImmutableRangeSet");
        addCopiedGuavaCollection("ImmutableRangeSet", range.resolve(), "of");
        addCopiedGuavaCollection("TreeRangeSet", rangeSet.resolve(), immutableRangeSet.resolve(), "create");
        addCopiedGuavaCollection("RangeSet", "TreeRangeSet", rangeSet.resolve(), immutableRangeSet.resolve(), "create");
    }

    private void addGoogleGuavaImmutableClasses() {
        addCopiedGuavaCollection("ImmutableCollection", "ImmutableList", Collection.class, Collection.class, "copyOf");
        addCopiedGuavaCollection("ImmutableList", Collection.class);
        addCopiedGuavaCollection("ImmutableMap", Map.class);
        addCopiedGuavaCollection("ImmutableSet", Collection.class);
        addCopiedGuavaCollection("ImmutableSortedMap", Map.class);
        addCopiedGuavaCollection("ImmutableSortedSet", Collection.class);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addNewGoogleGuavaClasses() {
        String rangeFqcn = GUAVA_PACKAGE + "Range";
        ConditionalInstantiator range = new ConditionalInstantiator(rangeFqcn, false);
        addFactory(range.resolve(), new ReflectiveGenericContainerFactory(rangeFqcn, "atLeast", Comparable.class));

        String optional = "com.google.common.base.Optional";
        addFactory(classForName(optional), new ReflectiveGenericContainerFactory(optional, "of", Object.class));
    }

    private void addJodaTimeClasses() {
        ConditionalInstantiator chronology = new ConditionalInstantiator(JODA_PACKAGE + "Chronology");
        addValues(chronology.resolve(),
                chronology.callFactory(JODA_PACKAGE + "chrono.GregorianChronology", "getInstanceUTC", classes(), objects()),
                chronology.callFactory(JODA_PACKAGE + "chrono.ISOChronology", "getInstanceUTC", classes(), objects()),
                chronology.callFactory(JODA_PACKAGE + "chrono.GregorianChronology", "getInstanceUTC", classes(), objects()));
        ConditionalInstantiator dateTimeZone = new ConditionalInstantiator(JODA_PACKAGE + "DateTimeZone");
        addValues(dateTimeZone.resolve(),
                dateTimeZone.callFactory("forOffsetHours", classes(int.class), objects(+1)),
                dateTimeZone.callFactory("forOffsetHours", classes(int.class), objects(-10)),
                dateTimeZone.callFactory("forOffsetHours", classes(int.class), objects(+1)));
        ConditionalInstantiator periodType = new ConditionalInstantiator(JODA_PACKAGE + "PeriodType");
        addValues(periodType.resolve(),
                periodType.callFactory("days", classes(), objects()),
                periodType.callFactory("hours", classes(), objects()),
                periodType.callFactory("days", classes(), objects()));
        ConditionalInstantiator yearMonth = new ConditionalInstantiator(JODA_PACKAGE + "YearMonth");
        addValues(yearMonth.resolve(),
                yearMonth.instantiate(classes(int.class, int.class), objects(2009, 6)),
                yearMonth.instantiate(classes(int.class, int.class), objects(2014, 7)),
                yearMonth.instantiate(classes(int.class, int.class), objects(2009, 6)));
        ConditionalInstantiator monthDay = new ConditionalInstantiator(JODA_PACKAGE + "MonthDay");
        addValues(monthDay.resolve(),
                monthDay.instantiate(classes(int.class, int.class), objects(6, 1)),
                monthDay.instantiate(classes(int.class, int.class), objects(6, 26)),
                monthDay.instantiate(classes(int.class, int.class), objects(6, 1)));
    }

    @SuppressWarnings("unchecked")
    private <T> void addValues(Class<T> type, Object red, Object black, Object redCopy) {
        prefabValues.addFactory(type, (T)red, (T)black, (T)redCopy);
    }

    private <T> void addFactory(Class<T> type, PrefabValueFactory<T> factory) {
        prefabValues.addFactory(type, factory);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addJavaFxCollection(String name, Class<?> copyFrom, String factoryMethod) {
        String className = JAVAFX_COLLECTIONS_PACKAGE + name;
        addFactory(classForName(className),
                new ReflectiveCollectionCopyFactory(className, copyFrom, JAVAFX_COLLECTIONS_PACKAGE + "FXCollections", factoryMethod));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addJavaFxProperty(String declaredType, String actualType, Class<?> propertyType) {
        addFactory(classForName(JAVAFX_PROPERTY_PACKAGE + declaredType),
                new ReflectiveJavaFxPropertyFactory(JAVAFX_PROPERTY_PACKAGE + actualType, propertyType));
    }

    private <T> void addNewGuavaCollection(String declaredType, String actualType) {
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>)classForName(GUAVA_PACKAGE + declaredType);
        ReflectiveCollectionFactory<T> factory =
                ReflectiveCollectionFactory.callFactoryMethod(GUAVA_PACKAGE + actualType, "create");
        addFactory(type, factory);
    }

    private <T, U> void addNewGuavaCollection(String declaredType, String actualType, Class<U> parameterType, U parameterValue) {
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>)classForName(GUAVA_PACKAGE + declaredType);
        ReflectiveCollectionFactory<T> factory =
                ReflectiveCollectionFactory.callFactoryMethodWithParameter(GUAVA_PACKAGE + actualType, "create", parameterType, parameterValue);
        addFactory(type, factory);
    }

    private <T> void addNewGuavaMap(String declaredType, String actualType) {
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>)classForName(GUAVA_PACKAGE + declaredType);
        ReflectiveMapFactory<T> factory = ReflectiveMapFactory.callFactoryMethod(GUAVA_PACKAGE + actualType, "create");
        addFactory(type, factory);
    }

    private <T> void addNewGuavaMap(String declaredType, String actualType, Comparator<Object> comparator) {
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>)classForName(GUAVA_PACKAGE + declaredType);
        ReflectiveMapFactory<T> factory =
                ReflectiveMapFactory.callFactoryMethodWithComparator(GUAVA_PACKAGE + actualType, "create", comparator);
        addFactory(type, factory);
    }

    private <T> void addNewGuavaTable(String declaredType, String actualType, Comparator<Object> comparator) {
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>)classForName(GUAVA_PACKAGE + declaredType);
        ReflectiveGuavaTableFactory<T> factory =
                ReflectiveGuavaTableFactory.callFactoryMethodWithComparator(GUAVA_PACKAGE + actualType, "create", comparator);
        addFactory(type, factory);
    }

    private <T> void addNewGuavaTable(String declaredType, String actualType) {
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>)classForName(GUAVA_PACKAGE + declaredType);
        ReflectiveGuavaTableFactory<T> factory =
                ReflectiveGuavaTableFactory.callFactoryMethod(GUAVA_PACKAGE + actualType, "create");
        addFactory(type, factory);
    }

    private void addCopiedGuavaCollection(String name, Class<?> copyFrom) {
        addCopiedGuavaCollection(name, copyFrom, "copyOf");
    }

    private void addCopiedGuavaCollection(String name, Class<?> copyFrom, String copyMethodName) {
        addCopiedGuavaCollection(name, copyFrom, copyFrom, copyMethodName);
    }

    private void addCopiedGuavaCollection(String name, Class<?> declaredCopyFrom, Class<?> actualCopyFrom, String copyMethodName) {
        addCopiedGuavaCollection(name, name, declaredCopyFrom, actualCopyFrom, copyMethodName);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addCopiedGuavaCollection(String declaredName, String actualName,
            Class<?> declaredCopyFrom, Class<?> actualCopyFrom, String copyMethodName) {

        String className = GUAVA_PACKAGE + actualName;
        addFactory(classForName(GUAVA_PACKAGE + declaredName),
                new ReflectiveCollectionCopyFactory(className, declaredCopyFrom, actualCopyFrom, className, copyMethodName));
    }
}
