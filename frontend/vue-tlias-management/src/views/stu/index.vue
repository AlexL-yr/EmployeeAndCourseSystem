<script setup>
import { onMounted, ref } from 'vue'
import { queryPageApi , addApi, queryInfoApi, updateApi, deleteApi, handleViolationApi} from '@/api/stu'
import { queryAllApi as queryAllClazzApi } from '@/api/clazz'
import { ElMessage, ElMessageBox } from 'element-plus'


//学历列表数据
const degrees = ref([{ name: '初中', value: 1 },{ name: '高中', value: 2 },{ name: '大专', value: 3 },{ name: '本科', value: 4 },{ name: '硕士', value: 5 },{ name: '博士', value: 6 }])
//性别列表数据
const genders = ref([{ name: '男', value: 1 }, { name: '女', value: 2 }])
//是否是院校学员列表数据
const whethers = ref([{ name: '是', value: 1 }, { name: '否', value: 0 }])
//搜索表单对象
let searchStu = ref({clazzId: '',degree: '',name: ''}) 
//列表展示数据
let tableData = ref([])

//钩子函数 - 页面加载时触发
onMounted(() => {
  queryPage()
  queryAllClazz()
})


//所有的班级数据
let clazzs = ref([])
//加载所有的班级数据
const queryAllClazz = async () => {
  let result = await queryAllClazzApi()
  if(result.code) {
    clazzs.value = result.data
  }
}


//复选框
let selectIds = ref([])
const handleSelectionChange = (selection) => {
  selectIds.value = selection.map(item => item.id)
}

//分页组件
const pagination = ref({currentPage: 1, pageSize: 10, total: 0})
//每页展示记录数发生变化时触发
const handleSizeChange = (pageSize) => {
  pagination.value.pageSize = pageSize
  queryPage()
}
//当前页码发生变化时触发
const handleCurrentChange = (page) => {
  pagination.value.currentPage = page
  queryPage()
}

//分页条件查询
const queryPage = async () => {
  const result = await queryPageApi(
      searchStu.value.clazzId,
      searchStu.value.degree,
      searchStu.value.name,
      pagination.value.currentPage,
      pagination.value.pageSize
  );

  if(result.code) {
    tableData.value = result.data.rows
    pagination.value.total = result.data.total
  }
}

//清空搜索栏
const clear = () => {
  searchStu.value = {clazzId:'', degree:'', name:'',}
  queryPage()
}


//----------- 新增 / 修改 ---------------------------
let dialogFormVisible = ref(false) //控制新增/修改的对话框的显示与隐藏
let labelWidth = ref(100) //form表单label的宽度
let formTitle = ref('') //表单的标题
let stu = ref({ //学生对象-表单数据绑定
  name: '',
  no: '',
  gender: '',
  phone: '',
  idCard: '',
  isCollege: '',
  address: '',
  degree: '',
  graduationDate: '',
  clazzId: ''
})

//清空表单
const clearStu = () => {
  stu.value = {
    name: '',
    no: '',
    gender: '',
    phone: '',
    idCard: '',
    isCollege: '',
    address: '',
    degree: '',
    graduationDate: '',
    clazzId: ''
  }
}

//新增学员
const addStu = () => {
  dialogFormVisible.value = true
  formTitle.value = '新增学员'
  clearStu()
}

//修改学生
const updateStu = async (id) => {
  clearStu()
  dialogFormVisible.value = true
  formTitle.value = '修改学员'
  let result = await queryInfoApi(id)
  if(result.code){
    stu.value = result.data
  }
}

//表单校验规则
const stuFormRef = ref()
const rules = ref({
  name: [
    { required: true, message: '姓名为必填项', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度为2-20个字', trigger: 'blur' }
  ],
  no: [
    { required: true, message: '学号为必填项', trigger: 'blur' },
    { min: 10, max: 10, message: '学号长度为10个字', trigger: 'blur' }
  ],
  gender: [{ required: true, message: '性别为必填项', trigger: 'change' }],
  phone: [
    { required: true, message: '手机号为必填项', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/g, message: '请输入合法的手机号', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '身份证号为必填项', trigger: 'blur' },
    { pattern: /^\d{18}$/g, message: '请输入合法的身份证号', trigger: 'blur' }
  ],
  isCollege: [
    { required: true, message: '是否院校学员为必填项', trigger: 'change' }
  ]
})

//重置表单
const resetForm = (stuForm) => {
  if (!stuForm) return
  stuForm.resetFields()
}

//-------------保存学生信息 
const save = (stuForm) => {
  //表单校验
  if(!stuForm) return
  stuForm.validate(async (valid) => {
    if(valid) {
      let api 
      if(stu.value.id) {
        api = updateApi(stu.value)
      }else {
        api = addApi(stu.value)
      }
      
      let result = await api
      if(result.code) {
        ElMessage.success('操作成功')
        dialogFormVisible.value = false
        queryPage()
      }else {
        ElMessage.error(result.msg)
      }
    }else {
      return false
    }
  })
}


//------- 删除学生
//根据ID删除单个学生
const delById = async (id) => {
  ElMessageBox.confirm('您确认删除此数据吗?' , '删除学生', {confirmButtonText:'确认', cancelButtonText:'取消', type:'warning'})
    .then(async () => {
      let result =  await deleteApi(`${id}`)
      if(result.code) {
        ElMessage.success('删除成功')
        queryPage()
      }else {
        ElMessage.error(result.msg)
      }
    }).catch(() => {
      ElMessage.info('取消删除')
    })
}

//批量删除员工
const delByIds = async () => {
  ElMessageBox.confirm('您确认删除此数据吗?' , '删除学生', {confirmButtonText:'确认', cancelButtonText:'取消',type:'warning'})
    .then(async () => {
      let result =  await deleteApi(selectIds.value.join(','))
      if(result.code) {
        ElMessage.success('删除成功')
        queryPage()
      }else {
        ElMessage.error(result.msg)
      }
    }).catch(() => {
      ElMessage.info('取消删除')
    })
}


const openViolation = (id) => {
  ElMessageBox.prompt('请输入违纪扣分', '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    inputPattern: /\d?/,
    inputErrorMessage: '数据无效',
  }).then(async (val) => {    
    let result = await handleViolationApi(id, val.value);
    if(result.code) {
      ElMessage.success('处理成功')
      queryPage()
    }else {
      ElMessage.error(result.msg)
    }
  })
}
</script>

<template>
    <!-- 顶部标题 -->
    <div>
      <div id="title">学员管理</div><br>
    </div>

    <!-- 条件搜索表单 -->
    <el-form :inline="true" :model="searchStu" class="demo-form-inline">
      <el-form-item label="姓名">
        <el-input v-model="searchStu.name" placeholder="请输入学生姓名"/>
      </el-form-item>

      <el-form-item label="最高学历">
        <el-select v-model="searchStu.degree" placeholder="请选择">
          <el-option v-for="degree in degrees" :label="degree.name" :value="degree.value" />
        </el-select>
      </el-form-item>

      <el-form-item label="所属班级">
        <el-select v-model="searchStu.clazzId" placeholder="请选择">
          <el-option v-for="clazz in clazzs" :label="clazz.name" :value="clazz.id" />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="queryPage()">查询</el-button>
        <el-button type="info" @click="clear()">清空</el-button>
      </el-form-item>
    </el-form>
    
    <!-- 功能按钮 -->
    <el-button type="success" @click="addStu();resetForm(stuFormRef)">+ 新增学员</el-button>
    <el-button type="danger" @click="delByIds()">- 批量删除</el-button>
    <br><br>
    
    
    <!-- 列表展示 -->
    <el-table :data="tableData" border style="width: 100%" fit @selection-change="handleSelectionChange">
      <el-table-column type="selection"  align="center" width="35" />
      <el-table-column prop="name" label="姓名" align="center" width="100px" />
      <el-table-column prop="no" label="学号" align="center" width="130px" />
      <el-table-column prop="clazzName" label="班级" align="center" width="160px"/>
      <el-table-column prop="gender" label="性别" align="center" width="70px" >
        <template #default="scope">
          {{ scope.row.gender == 1 ? '男': '女' }}
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" align="center" width="130px"/>
      <el-table-column prop="degree" label="最高学历" align="center" width="90px">
        <template #default="scope">
          <span v-if="scope.row.degree == 1">初中</span>
          <span v-else-if="scope.row.degree == 2">高中</span>
          <span v-else-if="scope.row.degree == 3">大专</span>
          <span v-else-if="scope.row.degree == 4">本科</span>
          <span v-else-if="scope.row.degree == 5">硕士</span>
          <span v-else-if="scope.row.degree == 6">博士</span>
          <span v-else>其他</span>
        </template>
      </el-table-column>
      <el-table-column prop="violationCount" label="违纪次数" align="center" width="85px" />
      <el-table-column prop="violationScore" label="违纪扣分" align="center" width="85px" />
      <el-table-column prop="updateTime" label="最后修改时间" align="center" width="170px"/>
      <el-table-column label="操作" align="center">
        <template #default="scope">
          <el-button type="primary" size="small" @click="updateStu(scope.row.id) ;resetForm(stuFormRef)">编辑</el-button>
          <el-button type="warning" size="small" @click="openViolation(scope.row.id)">违纪</el-button>
          <el-button type="danger" size="small" @click="delById(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <br>

    <!-- 分页组件Pagination -->
    <el-pagination
      v-model:current-page="pagination.currentPage"
      v-model:page-size="pagination.pageSize"
      :page-sizes="[5, 10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />



  
  <!-- 新增/修改学员对话框 -->
  <el-dialog v-model="dialogFormVisible" :title="formTitle" width="50%">
    <el-form :model="stu" ref="stuFormRef" :rules="rules">
      <!-- 第一行 -->
      <el-row>
        <el-col :span="12">
          <el-form-item label="姓名" :label-width="labelWidth" prop="name">
            <el-input v-model="stu.name" placeholder="请输入学员姓名"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="学号" :label-width="labelWidth" prop="no">
            <el-input v-model="stu.no" placeholder="请输入学员学号"/>
          </el-form-item>
        </el-col>
      </el-row>
      
      <!-- 第二行 -->
      <el-row>
        <el-col :span="12">
          <el-form-item label="性别" :label-width="labelWidth"  prop="gender">
            <el-select v-model="stu.gender" placeholder="请选择" style="width: 100%;">
              <el-option v-for="gender in genders" :label="gender.name" :value="gender.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="手机号" :label-width="labelWidth"  prop="phone">
            <el-input v-model="stu.phone" placeholder="请输入手机号"/>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 第三行 -->
      <el-row>
        <el-col :span="12">
          <el-form-item label="身份证号" :label-width="labelWidth"  prop="idCard">
            <el-input v-model="stu.idCard" placeholder="请输入身份证号"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="是否院校" :label-width="labelWidth" prop="isCollege">
            <el-select v-model="stu.isCollege" placeholder="请选择" style="width: 100%;">
              <el-option v-for="w in whethers" :label="w.name" :value="w.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 第四行 -->
      <el-row>
        <el-col :span="12">
          <el-form-item label="联系地址" :label-width="labelWidth">
            <el-input v-model="stu.address" placeholder="请输入联系地址"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="最高学历" :label-width="labelWidth">
            <el-select v-model="stu.degree" placeholder="请选择" style="width: 100%;">
              <el-option v-for="degree in degrees" :label="degree.name" :value="degree.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>


      <!-- 第五行 -->
      <el-row>
        <el-col :span="12">
          <el-form-item label="毕业时间" :label-width="labelWidth">
            <el-date-picker v-model="stu.graduationDate" type="date" placeholder="请选择毕业时间" value-format="YYYY-MM-DD" style="width: 100%;"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所属班级" :label-width="labelWidth">
            <el-select v-model="stu.clazzId" placeholder="请选择" style="width: 100%;">
              <el-option v-for="clazz in clazzs" :label="clazz.name" :value="clazz.id" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogFormVisible = false; resetForm(stuFormRef)">取消</el-button>
        <el-button type="primary" @click="save(stuFormRef)">保存</el-button>
      </span>
    </template>
  </el-dialog>


  <!-- 违纪处理 -->

</template>


<style scoped>
#title {
  font-size: 20px;
  font-weight: 600;
}
</style>