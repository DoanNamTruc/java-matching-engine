## Basic Core Matching
# Features

Latency: ~33 microseconds per order
(≈100,000 orders processed in 3–4 seconds)

Capacity: Up to 50,000,000 orders stored in off-heap memory

Snapshot & recovery support

Fast recovery: restore from snapshot only (no replay log re-execution)

# Technology Stack

Java 21

Netty for TCP connections

No framework, fixed heap size to ensure no heap pressure and avoid GC latency

Order book stored in native (off-heap) memory

JIT C2 compiles the hot execution path (requires warm-up)
![Uploading image.png…]()
