package com.aegis.kotlindemo.controller

import com.aegis.kotlindemo.model.entity.*
import com.aegis.kotlindemo.model.result.Result
import io.swagger.annotations.ApiOperation
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("entity")
class EntityController(val mongoTemplate: MongoTemplate) {

    @ApiOperation("查询实体类")
    @GetMapping("getClasses")
    fun getClasses(id: String): Result<ArrayList<EntityClass>?> {
        val res = mongoTemplate.find(
                Query.query(Criteria.where("moduleId").`is`(id)), EntityClass::class.java)
        return Result<ArrayList<EntityClass>?>(0).setData(ArrayList(res))
    }

    @ApiOperation("查询模块")
    @GetMapping("getModule")
    fun getModule(): Result<ArrayList<Module>> {
        val res = mongoTemplate.findAll(Module::class.java)
        return Result<ArrayList<Module>>(0).setData(ArrayList(res))
    }

    @ApiOperation("查询数据属性")
    @GetMapping("getDataProp")
    fun getDataProp(id: String): Result<DataProp?> {
        val res = mongoTemplate.findOne(
                Query.query(Criteria.where("moduleId").`is`(id)), DataProp::class.java)
        return Result<DataProp?>(0).setData(res)
    }

    @ApiOperation("查询对象属性")
    @GetMapping("getObjectProp")
    fun getObjectProp(id: String): Result<ObjectProp?> {
        val res = mongoTemplate.findOne(
                Query.query(Criteria.where("moduleId").`is`(id)), ObjectProp::class.java)
        return Result<ObjectProp?>(0).setData(res)
    }

    @ApiOperation("查询数据类型")
    @GetMapping("getDataType")
    fun getDataType(): Result<ArrayList<DataType>?> {
        val res = mongoTemplate.findAll(DataType::class.java)
        return Result<ArrayList<DataType>?>(0).setData(ArrayList(res))
    }


    @ApiOperation("新增或修改实体类")
    @PostMapping("creatOrUpdateClass")
    fun creatOrUpdateClass(@RequestBody entityClass: List<EntityClass>): Result<Int?> {
        for (it in entityClass) {
            val query = Query.query(Criteria.where("id").`is`(it.id))
            val update = Update()
            update.set("label", it.label)
            update.set("moduleId", it.moduleId)
            update.set("pid", it.pid)
            update.set("description", it.description)
            update.set("bandFlag", it.bandFlag)
            mongoTemplate.upsert(query, update, EntityClass::class.java)
        }
        return Result(0, "success")
    }

    @ApiOperation("删除实体类")
    @DeleteMapping("deleteClass")
    fun deleteClass(id: String): Result<Int?> {
        return try {
            val query = Query.query((Criteria.where("id").`is`(id)))
            val queryRes = mongoTemplate.findOne(query, EntityClass::class.java)
            if ("1" === queryRes!!.bandFlag){
                Result(500,"该实体类已被绑定，无法删除！")
            }else{
                mongoTemplate.remove(query, EntityClass::class.java)
                Result(0)
            }
        } catch (e: Exception) {
            Result(500, e.toString())
        }
    }

    @ApiOperation("新增或修改数据属性")
    @PostMapping("creatOrUpdateDataProp")
    fun creatOrUpdateDataProp(@RequestBody dataProp: DataProp): Result<Int?> {
        val query = Query.query(Criteria.where("moduleId").`is`(dataProp.moduleId))
        val update = Update.update("dataPropList", dataProp.dataPropList)
        mongoTemplate.upsert(query, update, DataProp::class.java)
        return Result(0, "success")
    }

    @ApiOperation("新增或修改对象属性")
    @PostMapping("creatOrUpdateObjectProp")
    fun creatOrUpdateObjectProp(@RequestBody objectProp: ObjectProp): Result<Int?> {
        val query = Query.query(Criteria.where("moduleId").`is`(objectProp.moduleId))
        val update = Update.update("objectPropList", objectProp.objectPropList)
        mongoTemplate.upsert(query, update, ObjectProp::class.java)
        return Result(0, "success")
    }

    @ApiOperation("新增或修改数据类型")
    @PostMapping("creatOrUpdateDataType")
    fun creatOrUpdateDataType(@RequestBody dataType: ArrayList<DataType>): Result<Int?> {
        mongoTemplate.dropCollection("data_type")
        mongoTemplate.insert(dataType, "data_type")
        return Result(0, "success")
    }

}