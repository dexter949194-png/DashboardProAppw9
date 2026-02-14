class ScanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val btn = Button(this)
        btn.text = "Powrót"
        btn.setOnClickListener { finish() }
        setContentView(btn)
    }
}
