A classic, grid-based block puzzle desktop application developed in Java Swing and Java2D. Players place diverse geometric block shapes onto an 8×8 board, clearing full rows and columns to accumulate points across multiple game modes.

---

## Technical Architecture & File Structure

The project follows a decoupled architecture separating data models, graphics rendering, user input, and resource management.

```text
BLOCKED.exe/
├── data/
│   └── highscore.txt           # Persistent best score storage
├── logs/
│   └── app.log                 # Runtime application event & error logging
├── src/
│   ├── model/
│   │   ├── Board.java          # Grid matrix, collision rules, line clearing algorithm
│   │   ├── BlockPiece.java     # Shape matrices, color properties, rotations
│   │   ├── PieceGenerator.java # Random 3-piece tray generator & fit validation
│   │   ├── GameMode.java       # Enum: CLASSIC, TIME_ATTACK
│   │   └── GameState.java      # Enum: MENU, PLAYING, PAUSED, GAME_OVER
│   ├── view/
│   │   ├── GameFrame.java      # Main JFrame container and screen layout manager
│   │   ├── GamePanel.java      # Main Java2D rendering panel and repaint loop
│   │   ├── UIOverlay.java      # Overlays for Main Menu, Pause, and Game Over screens
│   │   └── anim/
│   │       ├── IDrawable.java  # Interface for polymorphic rendering
│   │       ├── FlashAnimation.java # Line-clear visual effects
│   │       └── PopAnimation.java   # Piece placement snap/pop effects
│   ├── controller/
│   │   ├── InputHandler.java   # Drag-and-drop mouse & keyboard event listeners
│   │   ├── ScoreManager.java   # Score calculations and file I/O operations
│   │   └── SoundManager.java   # WAV audio clip playback and mute toggling
│   └── Main.java               # Application entry point
├── README.md
└── .gitignore