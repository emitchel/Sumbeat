package com.erm.artists


import org.junit.Test

class Golfer(val name: String, val handicap: Int)
class Team(val golfer1: Golfer, val golfer2: Golfer) {
    val ambrose = Math.round(((golfer1.handicap + golfer2.handicap) / 4).toDouble()).toInt()
    override fun toString(): String {
        return "${golfer1.name} + ${golfer2.name} = $ambrose"
    }
}

class Lineup(val teams: MutableList<Team>) {
    var standardDeviation: Double? = null

    init {
        val numArray = teams.map { it.ambrose.toDouble() }.toDoubleArray()
        val mean = numArray.average()
        val sd = numArray.fold(0.0, { accumulator, next -> accumulator + Math.pow(next - mean, 2.0) })
        standardDeviation = Math.sqrt(sd / numArray.size)
    }

    override fun toString(): String {
        var str = "Lineup Standard Deviation $standardDeviation"
        teams.forEach {
            str += "\n${it}"
        }
        return str
    }
}

class TempTests {

    var groupA = mutableListOf(
        Golfer("Elliot", 13),
        Golfer("Kyle", 12),
        Golfer("Travis", 16),
        Golfer("Jordan", 26),
        Golfer("Bret", 19)
    )

    var groupB = mutableListOf(
        Golfer("John", 36),
        Golfer("Cody", 41),
        Golfer("Tay", 35),
        Golfer("Chase", 38),
        Golfer("Jeff", 42)
    )

    var lineups = mutableListOf<Lineup>()
    @Test
    fun testOutcomes() {

        //i iterations
        for (i in 0 until 1000) {
            //shuffle
            groupA.shuffle()
            groupB.shuffle()
            //create teams
            val teams = arrayListOf<Team>()

            for (teammate in 0 until 5) {
                teams.add(Team(groupA[teammate], groupB[teammate]))
            }
            lineups.add(Lineup(teams))
            println("Added Lineup: \n${lineups.last()}")
        }
        val smallestDeviation = lineups.minBy { it.standardDeviation!!.toInt() }
        println("Smallest Deviation and best lineup is $smallestDeviation")

    }
}
