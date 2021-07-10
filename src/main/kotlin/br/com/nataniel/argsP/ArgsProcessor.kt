package br.com.nataniel.argsP

private const val defaultName = "\$DeFaUlT\$"

class ArgsProcessor(args: Array<String>) {
    private val map: Map<String, String>

    init {
        val mMap = mutableMapOf<String, String>()

        var key: String? = null

        args.forEach {
            if (key == null && it.startsWith("-")) {
                key = it
            } else if (key == null) {
                mMap.putWithoutKey(it)
            } else if (key != null) {
                mMap[key!!] = it
                key = null
            }
        }

        map = mMap
    }

    fun getAllDefault(): List<String> {
        val keys = map.keys.filter { it.startsWith(defaultName) }

        return keys.mapNotNull { map[it] }
    }

    fun contains(key: String): Boolean = map.containsKey(key)
    fun containsVariants(vararg key: String): Boolean {
        for (it in key) {
            val result = contains(it)
            if (result) {
                return true
            }
        }

        return false
    }

    fun get(key: String): String? = map[key]

    fun getOrDefault(key: String, default: String): String {
        if (map.containsKey(key)) {
            return map[key]!!
        } else {
            return default
        }
    }

    fun getVariantsOrDefault(vararg key: String, default: String): String {
        var value: String? = null

        for (it in key) {
            if (map.containsKey(it)) {
                value = map[it]
                break
            }
        }

        if (value == null) {
            return default
        } else {
            return value
        }

    }

    private fun <T> MutableMap<String, T>.putWithoutKey(value: T) {
        var ok = false
        repeat(Integer.MAX_VALUE) {
            val key = "${defaultName}_$it"
            if (!containsKey(key)) {
                put(key, value)
                ok = true
                return@repeat
            }
        }

        if (!ok) {
            throw Exception("Impossible add value in map")
        }
    }

}
