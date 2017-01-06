# nat-punching-code说明
*created on 2017/01/06*
## 顶层说明
本文件夹内存放着两个eclipse工程项目。

- PCDClient：用于客户端
- PCDServer：用于服务器端

## 两个工程项目的介绍
### 整体介绍
包名：`com.neil.client`、`com.neil.server`

该项目的构建过程是参考了[tking/JSTUN](https://github.com/tking/JSTUN)的一些基础性代码（同时部分代码来源于该项目），参照了STUN协议的[RFC3489文档](https://datatracker.ietf.org/doc/rfc3489/)。

同时，根据本项目的具体项目需求，该项目对上述两个参考进行了较大的修改和重构。

### 详细介绍
`\attr`：存放着信息交互时，数据包的属性（`MessageAttributes`），如：

- username
- password
- exchangedaddress
- mappedaddress
- localaddress
- ...

`\header`：与\attr类似地，存放着信息交互时，数据包的头部属性（`MessageHeader`）

- BindingRequest
- BindingResponse
- BindingErrorResponse
- SharedSecretRequest
- SharedSecretResponse
- SharedSecretErrorResponse

**Note：当前Header、Attribute使用情况如下：(仅作参考，今后肯定是需要进行大的修改的！！！代码可能跟下表不对应)**

<table>
    <thead>
        <tr>
            <th>Attributes</th>
            <th>BindingRequest</th>
			<th>BindingResponse</th>
			<th>BindingErrorResponse</th>
			<th>SharedSecretRequest</th>
			<th>SharedSecretResponse</th>
			<th>SharedSecretErrorResponse</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>username</td>
            <td>MUST</td>
			<td>NOT USE</td>
			<td>-</td>
			<td>MUST</td>
			<td>-</td>
			<td>-</td>
        </tr>
        <tr>
            <td>password</td>
            <td>MUST</td>
			<td>NOT USE</td>
			<td>-</td>
			<td>MUST</td>
			<td>-</td>
			<td>-</td>
        </tr>
 		<tr>
            <td>exchangedaddress</td>
            <td>MUST</td>
			<td>NOT USE</td>
			<td>-</td>
			<td>MUST</td>
			<td>-</td>
			<td>-</td>
        </tr>
		<tr>
            <td>mappedaddress</td>
            <td>MUST</td>
			<td>NOT USE</td>
			<td>-</td>
			<td>MUST</td>
			<td>-</td>
			<td>-</td>
        </tr>
		<tr>
            <td>localaddress</td>
            <td>MUST</td>
			<td>NOT USE</td>
			<td>-</td>
			<td>NOT USE</td>
			<td>-</td>
			<td>-</td>
        </tr>
    </tbody>
</table>

## 说明
本项目是课程设计的一部分，课程设计覆盖着整个大三上一学期。看似时间非常充裕，但是时间还是非常紧的。

- 一是，组内同学一开始对该项目涉及的知识知之甚少，需要花大量的时间去学习新东西。
- 二是，小组是临时建立的，需要一定的时间去磨合。
- 三是，老师能给的指导有限。
- 四是，组员们在平时都要上课、做实验等等，只能利用课余的少部分时间去写项目。
- 五是，项目结题要求和时间很尴尬。结题要求需要完整的中期、终期报告等，并且对报告的内容、字数等都做了严格的要求；结题答辩时间放在考试季，与复习时间和考试时间相冲突。
- 。。。

总之，目前项目不够完善，但是收获还是挺大的。待今后再做完善吧！希望这个项目能够真正走向实用的过程！！！