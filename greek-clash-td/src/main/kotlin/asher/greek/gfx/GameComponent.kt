package asher.greek.gfx

interface GameComponent {
    var isSelected: Boolean
}

object GameComponentNone : GameComponent {
    override var isSelected: Boolean = false
}