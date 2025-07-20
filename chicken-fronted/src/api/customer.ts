import request from '@/utils/request'

/**
 * 获取客户分页列表
 * @param params 分页查询DTO {page: 页码, pageSize: 每页数量, name: 客户姓名(可选)}
 * @returns Promise对象
 */
export const getCustomerPageListAPI = (params: {
  page: number
  pageSize: number
  name?: string
}) => {
  console.log('客户分页查询参数:', params)
  return request({
    url: '/user/page',
    method: 'get',
    params: {
      page: params.page,
      pageSize: params.pageSize,
      name: params.name || undefined // 如果name为空则不传该参数
    }
  })
}

/**
 * 根据id获取客户信息，用于回显
 * @param id 客户id
 * @returns Promise对象
 */
export const getCustomerByIdAPI = (id: number) => {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}

/**
 * 添加客户
 * @param params 添加客户的DTO对象
 * @returns Promise对象
 */
export const addCustomerAPI = (params: {
  name: string
  account: string
  phone: string
  gender: any
  pic?: string
}) => {
  return request({
    url: '/user/add',
    method: 'post',
    data: { ...params }
  })
}

/**
 * 修改客户信息
 * @param params 更新客户信息的DTO对象
 * @returns Promise对象
 */
export const updateCustomerAPI = (params: {
  id: number
  name: string
  phone: string
  gender: any
  pic?: string
}) => {
  console.log("让我看看参数是怎么个事儿")
  console.log(params)
  return request({
    url: '/user/update',
    method: 'put',
    data: { ...params}
  })
}

/**
 * 删除客户
 * @param id 客户id
 * @returns Promise对象
 */
export const deleteCustomerAPI = (id: number) => {
  return request({
    url: `/user/delete/${id}`,
    method: 'delete'
  })
}
