package com.sunya.verificabitur.reader

import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import java.io.File

fun readPrivateInfo(filename : String ) : PrivateInfo {
    println("readPrivateInfo filename = ${filename}")

    //gulp the entire file to a string
    val file = File(filename)
    val text = file.readText(Charsets.UTF_8)

    val serializer = serializer<PrivateInfo>() // use the default serializer

    // Create the configuration for (de)serialization
    val xml = XML { indent = 2 }

    val info : PrivateInfo = xml.decodeFromString(serializer, text)
    println("$info")
    return info
}

enum class HttpType { internal, external}
enum class Storage { ram, file}

@Serializable
@XmlSerialName(value = "private")
data class PrivateInfo(
    @XmlElement val version: String,
    @XmlElement val name: String,
    @XmlElement val dir: String,
    @XmlElement val rand: String,
    @XmlElement val cert: Int,
    @XmlElement val skey: String,
    @XmlElement val httpl: String,
    @XmlElement val httpdir: String,
    @XmlElement @XmlSerialName(value = "httptype") val httptype: HttpType,
    @XmlElement val hintl: String,
    @XmlElement val keygen: String,
    @XmlElement @XmlSerialName(value = "arrays") val arrays: Storage,
    @XmlElement val nizkp: String,
) {


    override fun toString(): String {
        return buildString {
            appendLine("PrivateInfo(version='$version'")
            appendLine("  name='$name', certainty=$cert, httptype=$httptype, storage=$arrays, nizkp=$nizkp")
            appendLine("  httpl='$httpl', hintl='$hintl'")
            appendLine("  workingDir='$dir'")
            appendLine("  httpdir='$httpdir'")
            appendLine("  rand='$rand'")
            appendLine("  skey='$skey'")
            appendLine("  keygen='$keygen'")
        }
    }
}

/*

<!-- ATTENTION! This is a private information file. It contains all your
     internal parameters of a protocol execution, including your secret
     signing key.

     WARNING! The contents of this file MUST REMAIN SECRET. Failure to do
     so may result in a catastrophic security breach.

     DO NOT edit this file during the execution of the protocol. If you
     must edit it, then please be VERY CAREFUL and MAKE SURE THAT YOU
     UNDERSTAND THE IMPLICATIONS OF YOUR EDITS.

     Many XML features are disabled and throw errors, so parsing is more
     restrictive than the schema implies. -->

<private>

   <!-- Version of Verificatum Software for which this info is intended. -->
   <version>3.1.0</version>

   <!-- Name of party. This must satisfy the regular expression [A-Za-z][A-
        Za-z0-9_ ]{1,255}. -->
   <name>Party01</name>

   <!-- Working directory of this protocol instance. WARNING! This field is
        not validated syntactically. -->
   <dir>/home/stormy/dev/verificatum-vmn-3.1.0-full/verificatum-vmn-3.1.0/demo/mixnet/mydemodir/Party01/dir</dir>

   <!-- Source of randomness (instance of com.verificatum.crypto.
        RandomSource). WARNING! This field is not validated syntactically
        and it is impossible to verify that a random device points to a
        source of randomness suitable for cryptographic use, or that a
        pseudo-random generator has been initialized with such randomness
        -->
   <rand>RandomDevice(/dev/urandom)::00000000020100000023636f6d2e766572696669636174756d2e63727970746f2e52616e646f6d446576696365010000000c2f6465762f7572616e646f6d</rand>

   <!-- Certainty with which probabilistically checked parameters are
        verified, i.e., the probability of an error is bounded by 2^(-
        cert). This must be a positive integer at most equal to 256. -->
   <cert>50</cert>

   <!-- Pair of public and private signature keys (instance of com.
        verificatum.crypto.SignatureKeyPair). WARNING! This field is not
        validated syntactically. -->
   <skey>SignatureKeyPair(SignaturePKeyHeuristic(RSA, bitlength=2048),SignatureSKeyHeuristic(RSA, bitlength=2048))::00000000020100000027636f6d2e766572696669636174756d2e63727970746f2e5369676e61747572654b65795061697200000000020000000002010000002d636f6d2e766572696669636174756d2e63727970746f2e5369676e6174757265504b65794865757269737469630000000002010000012630820122300d06092a864886f70d01010105000382010f003082010a0282010100a8656c8d3bdac6dddd33c590ac74ceca14567a9c0f7826779a3aa1b610c2c3ed92a1b6402c8cd39324101923569f7a660e7c1e0ce96c24c1e2a8b171529dbab760aba41818678c8b20352984bac167a7e46ae198443e8e30c0fbeb2e92e48ac86725df3cc3d9e6007ffd644c8d5d1325b06f7cb7e6bf39105d5f215c59935301eec6f97f4c9c62e9f80b8df9544528800f7c0ef866accd6acb159821e942579f05f7b449f7fe56c475c0e5a3e7a482b2d05c3f82474bb2e4a0d67e889b245e9493e11a805307565326f5130dff8a91be7a6cb987f59b11cf9e87af8b106371a054394ea8f78a0108a208a4926179a039dff4396a4f8539a730f2a9ff221befdf02030100010100000004000008000000000002010000002d636f6d2e766572696669636174756d2e63727970746f2e5369676e6174757265534b6579486575726973746963000000000201000004c1308204bd020100300d06092a864886f70d0101010500048204a7308204a30201000282010100a8656c8d3bdac6dddd33c590ac74ceca14567a9c0f7826779a3aa1b610c2c3ed92a1b6402c8cd39324101923569f7a660e7c1e0ce96c24c1e2a8b171529dbab760aba41818678c8b20352984bac167a7e46ae198443e8e30c0fbeb2e92e48ac86725df3cc3d9e6007ffd644c8d5d1325b06f7cb7e6bf39105d5f215c59935301eec6f97f4c9c62e9f80b8df9544528800f7c0ef866accd6acb159821e942579f05f7b449f7fe56c475c0e5a3e7a482b2d05c3f82474bb2e4a0d67e889b245e9493e11a805307565326f5130dff8a91be7a6cb987f59b11cf9e87af8b106371a054394ea8f78a0108a208a4926179a039dff4396a4f8539a730f2a9ff221befdf020301000102820100426b7ad4fc324f352c6e22b36d2a4774327067bd0d66f539409676b942c4279699bafa1136e1370476f97888d53e62ff45205494002fbd11d26e7a4ab9ece7bc33bf8fa24761f46fddbcca4b05848a7790e34d670b27e75ab88bc4d8226d4d863d151587b8b246039578232b04a91d07c51f3c40a71d6e8b136115de00a0d8e0aa9e46e11cad69a106fa6e6c9780a1e8cee050687b443b3c985a078e75b48ba65f5bc0e9b347849b5fc204907b730d267adb7509444f1e2f399abfc0a1ef497269be4f4948898a4f6badbac2e075cf6b4cd5410e6f471ad1b862c5adc4d7dccd83b53cb78ee44ec86e4b8663f0f14827a6a007bda7bb935e1fd71f07cc6d405902818100de0e7ad133a509b4a85a98baf8b34d75612717e2180fbfa0731e2bf859905870569935cb225b10fc25b9af2850859cec9f6896d235fbd1de4030eff6fa1793e65ac4e1ead7d19efafff429e268598c43794b69b24a72abc8162406231b11ceeec3c07b69728ad44e9c735fef8265cbe5f633de3365716cb733acc193cd2d965902818100c2231b177ea7a6f23aa07fd66d256da234d32c1cc5b7306a28ff7d63ba0ff51f0996ac832e249cf804dde5b33914f55b645b3ed50b366e9c8f996fbc4d24be94dbbf783e18404515556cb4b21525a4bcbc7bce55ba27ff1ab2ecc145e7aa6767170eca145bec655f47c888195bb86f2648f4ea57bc1920ab5db7370255e2e0f7028180563366c809755ad42fbaa3a9895c0988b4833989426ff2a2b5ad93c21ffaa1ea5223bdb7328a0988e898317fc3ea6a658ce84c0c247ab218c5f07966f5e4eb3c342653d117a0bf478eced8e7943c96efa68978e9866f07726fede21804ad20189e12fd958caa8a0a4e3f9791619c64cfcb888d0c84a7c85d420921486010ff590281804d58ed4f540ff9cce29cd5b219f46294d0d51deb2cbf0ad4111791deacdff4ba73f88b2d0cb25bb3d9448b62f7a829054b9bab11f890ac4b464f4c9c4a640c668492e9965bd527711382e70f58ab91d1fc8a9b2fbea676d62d5974bba44c593c528c7ae8d7a2fcd494660a0b8866982a39c112a8f7f14ef9d7b1ca81ecb4230b028181008380c8f89b04cebe08d9f033f09d7148ed2b5ef268e233881dc96c410739979f9bd9f43af5c9a1d0e39174092d1b14a34a5c49793fafa6e2b44403afbaae0ad94639a06634c82fe6fa4a9863aa64793b3c344d88565c97a9d86b54e5471fa1931dfd2f7c5c651702baedbda6ea4ebabf0da1eb5ab48f6d25246af809ae261884010000000400000800</skey>

   <!-- URL where the HTTP-server of this party listens for connections,
        which may be different from the HTTP address used to access it, e.
        g., if it is behind a NAT. -->
   <httpl>http://localhost:8041</httpl>

   <!-- Root directory of HTTP server. WARNING! This field is not validated
        syntactically. -->
   <httpdir>/home/stormy/dev/verificatum-vmn-3.1.0-full/verificatum-vmn-3.1.0/demo/mixnet/mydemodir/Party01/httproot</httpdir>

   <!-- Decides if an internal or external HTTP server is used. Legal
        values are "internal" or "external". -->
   <httptype>internal</httptype>

   <!-- Socket address given as <hostname>:<port> or <ip address>:<port>,
        where our hint server listens for connections, which may be
        different from the address used to access it, e.g., if it is behind
        a NAT. -->
   <hintl>localhost:4041</hintl>

   <!-- Determines the key generation algorithm used to generate keys for
        the CCA2-secure cryptosystem with labels used in subprotocols. An
        instance of com.verificatum.crypto.CryptoKeyGen. WARNING! This
        field is not validated syntactically. -->
   <keygen>CryptoKeyGenNaorYung(ECqPGroup(P-521),HashfunctionHeuristic(SHA-512))::0000000002010000002b636f6d2e766572696669636174756d2e63727970746f2e43727970746f4b657947656e4e616f7259756e67000000000300000000020100000020636f6d2e766572696669636174756d2e61726974686d2e4543715047726f75700100000005502d3532310000000002010000002c636f6d2e766572696669636174756d2e63727970746f2e4861736866756e6374696f6e48657572697374696301000000075348412d353132010000000400000100</keygen>

   <!-- Determines if arrays of group/field elements and integers are
        stored in (possibly virtual) RAM or on file. The latter is only
        slighly slower and can accomodate larger arrays ("ram" or "file").
        -->
   <arrays>file</arrays>

   <!-- Destination directory for non-interactive proof. Paths are relative
        to the working directory or absolute. WARNING! This field is not
        validated syntactically. -->
   <nizkp>nizkp</nizkp>

</private>

 */