package pt.isel.leic.daw.explodingbattleships.data.db

/**
 * Processes the list received from the database and returns true if there were more items in the result set
 * @param receivedList the list received from the database
 * @param finalList the final to be return to the calling function
 * @param limit the maximum size of the final list
 * @return a [Boolean] if there were more items in the result set
 */
fun <T> getHasMoreAndProcessList(receivedList: List<T>, finalList: MutableList<T>, limit: Int): Boolean {
    var found = 0
    receivedList.forEach {
        found++
        if (found <= limit) {
            finalList.add(it)
        } else {
            return true
        }
    }
    return false
}