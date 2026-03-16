# 🗂️ Portfolio

A personal portfolio website with an AI-powered chat assistant that can answer questions about the owner's experience, skills, and projects.

The frontend is built with **Kotlin/Wasm** using Compose Multiplatform, and the backend is a **Ktor** server powered by **Gemini AI** with vector search via **Qdrant**.

---

## 🛠️ Tech Stack

### Frontend
| Technology | Purpose |
|---|---|
| [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) (Wasm/JS) | UI framework |
| [Ktor Client](https://ktor.io/docs/client-overview.html) | HTTP & WebSocket communication |
| [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) | JSON parsing |
| [Coil](https://coil-kt.github.io/coil/) | Image loading |
| [Rive](https://rive.app/) | Bot animation |

### Backend
| Technology | Purpose |
|---|---|
| [Ktor Server](https://ktor.io/) (Netty) | REST API & WebSocket server |
| [Google Gemini AI](https://ai.google.dev/) | AI chat generation & text embeddings |
| [Qdrant](https://qdrant.tech/) | Vector database for semantic search (RAG) |
| [Kotlin Telegram Bot](https://github.com/kotlin-telegram-bot/kotlin-telegram-bot) | Telegram bot for content management |
| [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) | JSON serialization |
| [Logback](https://logback.qos.ch/) | Logging |

### Shared
| Technology | Purpose |
|---|---|
| Kotlin Multiplatform | Shared API models between frontend and backend |
| Kotlinx DateTime | Date/time utilities |