package org.cryptobiotic.pep

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.unwrap
import electionguard.core.ElGamalCiphertext
import electionguard.core.ElementModP
import electionguard.core.productionGroup
import electionguard.publish.makeConsumer
import org.cryptobiotic.verificabitur.bytetree.ByteTreeNode
import org.cryptobiotic.verificabitur.bytetree.readByteTreeFromFile
import kotlin.test.Test
import kotlin.test.assertNotNull

// check that the mixnet output decrypts properly
class MixnetDecryptorTest {
    val group = productionGroup()

    val inputDir = "src/test/data/working"
    val workingDir =  "testOut/testDecryptMixnetOutput"
    val trusteeDir =  "$inputDir/eg/trustees"
    val invalidDir =  "${workingDir}/invalid"
    val inputBallots = "$inputDir/bb/vf/mix1/Ciphertexts.bt"
    val mixedBallots = "$inputDir/bb/vf/mix2/ShuffledCiphertexts.bt"

    @Test
    fun testCompareMixnet() {
        val root = readByteTreeFromFile(inputBallots)
        val ptree = convertByteTree(root.root)

        val consumer = makeConsumer(group, "src/commonTest/data/rave/eg")
        val result = consumer.readEncryptedBallot(
            "src/commonTest/data/rave/bb/EB",
            "id318732082"
        )
        if (result is Err) {
            println(result.error)
        } else {
            val eballot = result.unwrap()
            var count = 1
            eballot.contests.forEach {
                it.selections.forEach {
                    val ciphertext = it.encryptedVote
                    val where = ptree.findCiphertext(ciphertext.pad)
                    if (where != null) {
                        println("$count found ${it.selectionId} in $where")
                    } else {
                        println("$count not found ${it.selectionId}")
                    }
                    count++
                }
            }
        }
    }

    @Test
    fun testCiphertextDecryptor() {
        val decryptor = CiphertextDecryptor(
            group,
            "src/commonTest/data/rave/eg",
            "src/commonTest/data/rave/eg/trustees",
        )

        val consumer = makeConsumer(group, "src/commonTest/data/rave/eg")
        val result = consumer.readEncryptedBallot(
            "src/commonTest/data/rave/bb/EB",
            "id318732082"
        )
        if (result is Err) {
            println(result.error)
        } else {
            val eballot = result.unwrap()
            var count = 1
            eballot.contests.forEach {
                it.selections.forEach {
                    val ciphertext = it.encryptedVote
                    val vote = decryptor.decrypt(ciphertext)
                    println("$count ${it.selectionId} vote $vote")
                    assertNotNull(vote)
                    count++
                }
            }
        }
    }

    @Test
    fun testDecryptMixnetOutput() {

        val root = readByteTreeFromFile(mixedBallots)
        val ptree = convertByteTree(root.root)
        println(ptree)
        val ctree = convertPTree(ptree)
        println(ctree)

        val decryptor = CiphertextDecryptor(
            group,
            "src/commonTest/data/rave/eg",
            "src/commonTest/data/rave/eg/trustees",
        )

        decryptor.checkCipherTextDecrypts(ctree)
    }

    fun CiphertextDecryptor.checkCipherTextDecrypts(ctree : CTree) {
        if (ctree.ciphertext != null) {
            val vote = this.decrypt(ctree.ciphertext)
            println("${ctree.name} vote $vote")
            assertNotNull(vote)
        } else {
            ctree.children.forEach { this.checkCipherTextDecrypts(it) }
        }
    }

    data class PTree(val name: String, val modp: ElementModP?) {
        val children = mutableListOf<PTree>()

        fun findCiphertext(wantp: ElementModP) : PTree? {
            if (modp != null && modp == wantp) return this
            children.forEach {
                val found = it.findCiphertext(wantp)
                if (found != null) return found
            }
            return null
        }
    }

    fun convertByteTree(node: ByteTreeNode): PTree {
        val ptree = PTree(node.name, convertContent(node.content))
        node.child.forEach { child ->
            ptree.children.add(convertByteTree(child))
        }
        return ptree
    }

    fun convertContent(content: ByteArray?): ElementModP? {
        if (content == null) return null
        // remove first byte
        val n = content.size
        val ba = ByteArray(n - 1) { it -> content[it + 1] }
        val p = group.binaryToElementModP(ba)
        return p
    }

    data class CTree(val name: String, val ciphertext: ElGamalCiphertext?) {
        val children = mutableListOf<CTree>()

        fun add(pad: PTree, data: PTree) {
            if (pad.modp != null && data.modp != null) {
                children.add(CTree(pad.name, ElGamalCiphertext(pad.modp, data.modp)))
            } else {
                require(pad.children.size == data.children.size)
                val result = CTree(pad.name, null)
                pad.children.zip(data.children).forEach { (padc, datac) ->
                    result.add(padc, datac)
                }
                children.add(result)
            }
        }
    }

    fun convertPTree(root: PTree): CTree {
        require(root.children.size == 2)
        val pad = root.children[0]
        val data = root.children[1]
        val ctree = CTree(root.name, null)
        ctree.add(pad, data)
        return ctree
    }
}